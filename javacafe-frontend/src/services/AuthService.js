export const login = async (email, password) => {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Login failed');
    }

    const data = await response.json();
    return data.token;
};

export const register = async (email, password, firstName, lastName) => {
    const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password, firstName, lastName })
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Registration failed');
    }

    const data = await response.json();
    return data.token;
};

