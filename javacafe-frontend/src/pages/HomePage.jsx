import FeaturedItem from '../components/FeaturedItem';
import Announcement from '../components/Announcement';
import { Link } from 'react-router-dom';

function HomePage() {
    // Announcements data
    const announcements = [
        {
            id: 1,
            image: 'https://images.unsplash.com/photo-1603912699214-92627f304eb6?w=900&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Z3JhbmQlMjBvcGVuaW5nfGVufDB8fDB8fHww',
            title: 'GRAND OPENING!!!',
            description: 'Java Café is now open and pouring happiness one cup at a time. Come enjoy fresh brews, cozy vibes, and delicious treats. Come celebrate with us and taste the difference!'
        },
        {
            id: 2,
            image: 'https://images.unsplash.com/photo-1600093463592-8e36ae95ef56?w=900&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Y29mZmVlJTIwc2hvcHxlbnwwfHwwfHx8MA%3D%3D',
            title: 'Multiple Locations',
            description: 'Visit one of our many DFW locations! With shops in Plano, Euless, Allen, Frisco, and Flower Mound we aim to provide you with delicious treats no matter where you go!! '
        },
        {
            id: 3,
            image: 'https://plus.unsplash.com/premium_vector-1720532759341-feb0cfea424d?w=352&dpr=2&h=367&auto=format&fit=crop&q=60&ixlib=rb-4.1.0',
            title: 'Java Café’s 25 Days of Cheer!',
            description: 'All season long, we’re picking 3 guests each day to win a free drink or sweet treat. Stop in, enjoy the festive atmosphere, and you might leave with a little extra Christmas joy!'
        }
    ];
    // Sample featured items data - you can replace this with data from an API
    const featuredItems = [
        {
            id: 1,
            image: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=300&fit=crop',
            title: 'Chocalate Cake',
            description: 'Delicious, moist chocalate cake with chocalate chips inside. ',
            linkText: 'View Cakes',
            linkTo: '/api/menu'
        },
        {
            id: 2,
            image: 'https://www.biggerbolderbaking.com/wp-content/uploads/2020/11/Cheese-Danish-thumbnail-scaled.jpg',
            title: 'Cheese Danish',
            description: 'Sweet pastry featuring flaky, buttery dough folded around a rich, tangy, cheesecake-like filling, finished with a sweet glaze, creating a delightful contrast of textures and sweet-savory flavors. ',
            linkText: 'View Pastries',
            linkTo: '/api/menu'
        },
        {
            id: 3,
            image: 'https://www.simplytrinicooking.com/wp-content/uploads/red-velvet-cake-500x500.jpg',
            title: 'Red Velvet Cake',
            description: 'Our version has a dramatic vibrant red color, velvety-soft texture, and unique sweet-and-tangy flavor profile. Paired with a rich, white cream cheese frosting. ',
            linkText: 'View Cakes',
            linkTo: '/api/menu'
        },
        {
            id: 4,
            image: 'https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&h=300&fit=crop',
            title: 'Chocalate Chip Cookies',
            description: 'Cookies with a rich, buttery, vanilla-flavored dough base with both chocolate chips AND chunks, which soften into melty pockets during baking. ',
            linkText: 'View Cookies',
            linkTo: '/api/menu'
        },
        {
            id: 5,
            image: 'https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=400&h=300&fit=crop',
            title: 'Blueberry Muffin',
            description: 'Sweet, soft baked treat with a moist, fluffy crumb, studded with juicy, sweet-tart blueberries that burst with flavor, topped with a sugary crust for a delightful contrast in textures. ',
            linkText: 'View Muffins',
            linkTo: '/api/menu'
        },
        {
            id: 6,
            image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR5yovUHhG2rFJES4xSlgu8FGSjb9xOdmYcOg&s',
            title: 'Mocha',
            description: 'Espresso-based drink that combines espresso, chocolate syrup or cocoa powder, and steamed milk. ',
            linkText: 'View Drinks',
            linkTo: '/api/menu'
        }
    ];

    return (
        <div>
            <div className="bg-yellow-700 text-white py-3 shadow-md mb-6">
                <div className="container mx-auto px-4">
                    <p className="text-center text-lg font-medium">
                        Serving Fresh Coffee, Not Runtime Errors
                    </p>
                </div>
            </div>

            <section className="mb-12">
                    <div className="max-w-5xl mx-auto">
                        {announcements.map((announcement, index) => (
                            <Announcement 
                                key={announcement.id} 
                                announcement={announcement}
                                imageOnLeft={index % 2 === 0}
                            />
                        ))}
                    </div>
                </section>

            <div className="container mx-auto px-4 py-8">
                <section className="mb-12">
                    <h2 className="text-3xl font-bold text-amber-900 text-center mb-8">
                        Featured Items
                    </h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {featuredItems.map((item) => (
                            <FeaturedItem key={item.id} item={item} />
                        ))}
                    </div>
                </section>
                
                <div className="flex justify-center mt-12 mb-8">
                    <Link 
                        to="/api/menu"
                        className="w-full max-w-md bg-amber-900 text-white px-8 py-4 rounded-lg hover:bg-amber-800 transition-colors duration-200 text-center font-semibold text-lg shadow-md hover:shadow-lg"
                    >
                        View Full Menu
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default HomePage;
