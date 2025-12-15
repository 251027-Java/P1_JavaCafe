import { useState } from 'react';

// === CONFIGURATION START ===
const backendUrl = 'http://localhost:8080';
// === CONFIGURATION END ===

function ContactPage() {
    // 1. State for form inputs (Matches ContactSubmissionWOIDDTO fields)
    const [formData, setFormData] = useState({
        firstname: '',
        lastname: '',
        phone: '', // New field
        email: '',
        subject: '',
        message: '',
    });

    // 2. State for submission status and messages
    const [status, setStatus] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Handles changes to any input field
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    // Handles form submission to the backend controller
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Basic validation: Check required fields (FirstName, Email, Message)
        if (!formData.firstname || !formData.email || !formData.message) {
            setStatus('Please fill out all required fields (*).');
            return;
        }

        setIsSubmitting(true);
        setStatus('');

        try {
            const response = await fetch(`${backendUrl}/api/contact/submit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                // Send the formData object, which now matches ContactSubmissionWOIDDTO
                body: JSON.stringify(formData),
            });

            setIsSubmitting(false);

            if (response.status === 201) { // HTTP 201 Created
                setStatus('🥳 Success! Your message has been sent. Thank you for contacting us.');
                // Clear the form on successful submission
                setFormData({
                    firstname: '',
                    lastname: '',
                    phone: '',
                    email: '',
                    subject: '',
                    message: '',
                });
            } else if (response.status === 400) { // HTTP 400 Bad Request
                // Backend validation failed (e.g., incorrect format)
                setStatus('🚫 Submission failed. Please check your input and try again.');
            } else {
                setStatus(`⚠️ An unexpected server error occurred (Status: ${response.status}).`);
            }
        } catch (error) {
            console.error('Submission Error:', error);
            setIsSubmitting(false);
            setStatus('🚨 Network error. Could not connect to the server.');
        }
    };

    return (
        <div className="container mx-auto px-4 py-8 max-w-2xl">
            <h1 className="text-3xl font-serif font-bold text-amber-900 mb-6 border-b pb-2">
                Contact Us
            </h1>

            <form 
                onSubmit={handleSubmit} 
                className="bg-white p-6 rounded-lg shadow-xl border border-amber-200"
            >
                <p className="text-gray-600 mb-6">
                    We'd love to hear from you. Please fill out the form below. Required fields are marked with (*).
                </p>

                {/* Status Message Display */}
                {status && (
                    <div className={`p-3 mb-4 rounded-lg text-sm ${
                        status.includes('Success') 
                            ? 'bg-green-100 text-green-700 border border-green-300' 
                            : 'bg-red-100 text-red-700 border border-red-300'
                    }`}>
                        {status}
                    </div>
                )}
                
                {/* First Name & Last Name Fields (Side-by-side) */}
                <div className="flex gap-4 mb-4">
                    <div className="flex-1">
                        <label htmlFor="firstname" className="block text-sm font-medium text-gray-700">
                            First Name *
                        </label>
                        <input
                            type="text"
                            id="firstname"
                            name="firstname"
                            value={formData.firstname}
                            onChange={handleChange}
                            required
                            className="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-amber-500 focus:border-amber-500"
                        />
                    </div>
                    <div className="flex-1">
                        <label htmlFor="lastname" className="block text-sm font-medium text-gray-700">
                            Last Name
                        </label>
                        <input
                            type="text"
                            id="lastname"
                            name="lastname"
                            value={formData.lastname}
                            onChange={handleChange}
                            className="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-amber-500 focus:border-amber-500"
                        />
                    </div>
                </div>

                {/* Email Field */}
                <div className="mb-4">
                    <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                        Email *
                    </label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                        className="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-amber-500 focus:border-amber-500"
                    />
                </div>
                
                {/* Phone Field */}
                <div className="mb-4">
                    <label htmlFor="phone" className="block text-sm font-medium text-gray-700">
                        Phone Number
                    </label>
                    <input
                        type="tel" // Use type="tel" for phone numbers
                        id="phone"
                        name="phone"
                        value={formData.phone}
                        onChange={handleChange}
                        className="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-amber-500 focus:border-amber-500"
                    />
                </div>

                {/* Subject Field */}
                <div className="mb-4">
                    <label htmlFor="subject" className="block text-sm font-medium text-gray-700">
                        Subject
                    </label>
                    <input
                        type="text"
                        id="subject"
                        name="subject"
                        value={formData.subject}
                        onChange={handleChange}
                        className="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-amber-500 focus:border-amber-500"
                    />
                </div>

                {/* Message Field */}
                <div className="mb-6">
                    <label htmlFor="message" className="block text-sm font-medium text-gray-700">
                        Message *
                    </label>
                    <textarea
                        id="message"
                        name="message"
                        rows="4"
                        value={formData.message}
                        onChange={handleChange}
                        required
                        className="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-amber-500 focus:border-amber-500"
                    ></textarea>
                </div>

                {/* Submit Button */}
                <button
                    type="submit"
                    disabled={isSubmitting}
                    className={`w-full py-3 px-4 border border-transparent rounded-md shadow-sm text-lg font-medium text-white 
                        ${isSubmitting 
                            ? 'bg-amber-400 cursor-not-allowed' 
                            : 'bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500'
                        } transition duration-150`}
                >
                    {isSubmitting ? 'Sending...' : 'Send Message'}
                </button>
            </form>
        </div>
    );
};

export default ContactPage;