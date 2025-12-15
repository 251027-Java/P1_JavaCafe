export const getMenuProducts = async () => {
    try {
        const response = await fetch('/api/menu');
        if (!response.ok) {
            const errorText = await response.text().catch(() => 'Unknown error');
            throw new Error(`Failed to fetch menu products: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    } catch (error) {
        if (error.message.includes('fetch')) {
            throw new Error('Network error: Unable to connect to backend. Make sure the Spring Boot server is running on port 8080.');
        }
        throw error;
    }
};

export const getProductDescription = async (productId) => {
    const response = await fetch(`/api/menu/description/${productId}`);
    if (!response.ok) {
        throw new Error('Failed to fetch product description');
    }
    return response.json();
};

