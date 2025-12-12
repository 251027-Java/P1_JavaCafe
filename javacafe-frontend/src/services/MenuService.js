export const getMenuProducts = async () => {
    const response = await fetch('/api/menu');
    if (!response.ok) {
        throw new Error('Failed to fetch menu products');
    }
    return response.json();
};

export const getProductDescription = async (productId) => {
    const response = await fetch(`/api/menu/description/${productId}`);
    if (!response.ok) {
        throw new Error('Failed to fetch product description');
    }
    return response.json();
};

