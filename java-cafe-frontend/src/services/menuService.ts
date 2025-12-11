// src/services/menuService.ts

import apiClient from './api'; // Assumes you kept the apiClient setup

// Define the type for a public product—CRITICAL for TypeScript safety!
export interface MenuProduct {
    productId: number;
    category: string;
    name: string;
    basePrice: number;
    availability: 'IN_STOCK' | 'OUT_OF_STOCK'; // Example enum type
    // Add any other fields from your Spring Boot Menu DTO
}

export const fetchPublicMenu = async (): Promise<MenuProduct[]> => {
    try {
        // GET request to the public menu endpoint
        const response = await apiClient.get<MenuProduct[]>('/menu');
        return response.data;
    } catch (error) {
        console.error("Error fetching menu data:", error);
        throw error;
    }
};