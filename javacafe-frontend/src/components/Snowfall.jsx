function Snowfall() {
    const snowflakes = Array.from({ length: 50 }, (_, i) => ({
        id: i,
        left: Math.random() * 100,
        animationDuration: Math.random() * 5 + 3,
        animationDelay: Math.random() * 2,
        fontSize: Math.random() * 10 + 10,
        opacity: Math.random() * 0.5 + 0.5,
    }));

    return (
        <div 
            className="fixed top-0 left-0 w-full h-full pointer-events-none z-0 overflow-hidden"
            style={{ 
                background: 'transparent'
            }}
        >
            <style>{`
                @keyframes snowfall {
                    0% {
                        transform: translateY(-100vh) rotate(0deg);
                        opacity: 1;
                    }
                    100% {
                        transform: translateY(100vh) rotate(360deg);
                        opacity: 0;
                    }
                }
                
                .snowflake {
                    position: absolute;
                    top: -10px;
                    color: white;
                    text-shadow: 0 0 5px rgba(255, 255, 255, 0.8);
                    animation: snowfall linear infinite;
                    user-select: none;
                    pointer-events: none;
                    font-size: 10px;
                }
            `}</style>
            {snowflakes.map((flake) => (
                <div
                    key={flake.id}
                    className="snowflake"
                    style={{
                        left: `${flake.left}%`,
                        animationDuration: `${flake.animationDuration}s`,
                        animationDelay: `${flake.animationDelay}s`,
                        fontSize: `${flake.fontSize}px`,
                        opacity: flake.opacity,
                    }}
                >
                    ❄
                </div>
            ))}
        </div>
    );
}

export default Snowfall;

