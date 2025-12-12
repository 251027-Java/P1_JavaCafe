const ContactService = {
    baseUrl: "http://localhost:8080/api/contact",

    async submitContact(contactData) {
        try {
            const response = await fetch(this.baseUrl, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(contactData)
            });

            if (!response.ok) {
                throw new Error("Failed to submit contact form");
            }
            return await response.json();
        } catch (error) {
            console.error("Error submitting contact form:", error);
            throw error;
        }
    }
}