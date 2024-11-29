const Dashboard = () => {
    const handleLogout = async () => {
        try {
            await axios.post("/api/auth/logout");
            console.log("Logout successful");
            window.location.href = "/login"; // Redirect to login page
        } catch (err) {
            console.error("Logout failed", err);
        }
    };

    return (
        <div>
            <h1>Dashboard</h1>
            <button onClick={handleLogout}>Logout</button>
        </div>
    );
};

export default Dashboard;
