// Image mapping function for products
// This function maps product names and categories to their corresponding image paths
// Uses import.meta.glob to import all PNG files from assets folder

// Import all PNG files from all category subdirectories
// The { eager: true } option loads all images at build time
const imageModules = import.meta.glob('./**/*.png', { eager: true });

// Create a map of image paths to their imported URLs
const imageMap = {};
Object.keys(imageModules).forEach(path => {
    const pathParts = path.split('/');
    if (pathParts.length >= 3) {
        const category = pathParts[pathParts.length - 2]; // e.g., 'coffee' (second to last)
        const filenameWithExt = pathParts[pathParts.length - 1]; // e.g., 'espresso.png'
        const filename = filenameWithExt.replace('.png', ''); // e.g., 'espresso'
        const key = `${category}/${filename}`;
        // Handle both default export and direct export
        const imageUrl = imageModules[path].default || imageModules[path];
        imageMap[key] = imageUrl;
    }
});

// Debug: Log available images from PNG files
// This shows all PNG images that were successfully imported from the assets folder
if (Object.keys(imageMap).length > 0) {
    console.log('Loaded product images from PNG files:', Object.keys(imageMap).length, 'images');
}

/**
 * Get the image path for a product based on its name and category
 * @param {string} productName - The name of the product
 * @param {string} category - The category of the product
 * @returns {string|null} - The image URL or null if not found
 */
export const getProductImage = (productName, category) => {
    if (!productName || !category) {
        return null;
    }

    // Normalize inputs
    const normalizedCategory = category.toUpperCase();
    const normalizedName = productName.trim();

    // Map of category to image directory
    const categoryToDir = {
        'COFFEE': 'coffee',
        'CUPCAKES': 'cupcakes',
        'CROISSANTS': 'croissants',
        'COOKIES': 'cookies',
        'SANDWICHES': 'sandwiches',
        'SALADS': 'salads',
        'SMOOTHIES': 'smoothies',
        'PASTRIES': 'pastries',
        'BEVERAGES': 'beverages',
    };

    const imageDir = categoryToDir[normalizedCategory];
    if (!imageDir) {
        return null;
    }

    // Convert product name to a filename-friendly format
    // Example: "Java House Espresso" -> "java-house-espresso"
    const imageName = normalizedName
        .toLowerCase()
        .replace(/\s+/g, '-')           // Replace spaces with hyphens
        .replace(/[^a-z0-9-]/g, '')     // Remove special characters
        .replace(/-+/g, '-')            // Replace multiple hyphens with single
        .replace(/^-|-$/g, '');         // Remove leading/trailing hyphens

    // Try to find the image in the map (images are imported from PNG files at build time)
    const imageKey = `${imageDir}/${imageName}`;
    const imageUrl = imageMap[imageKey];

    if (imageUrl) {
        return imageUrl;
    }

    // If exact match not found, try case-insensitive search
    const lowerImageKey = imageKey.toLowerCase();
    for (const key in imageMap) {
        if (key.toLowerCase() === lowerImageKey) {
            return imageMap[key];
        }
    }

    // No matching PNG file found in assets folder
    return null;
};

export default getProductImage;
