# 🩸 Blood Donation Management System

![pic1](pic.png)

![pic2](pic.png)

## 🌟 Overview
A comprehensive Java desktop application for managing blood donation operations. This system streamlines the process of blood donation management, making it easier for both donors and administrators to contribute to saving lives.

## ✨ Features
- 🔐 Secure User Authentication
- 👥 User & Admin Dashboards
- 💉 Blood Donation Management
- 📊 Inventory Tracking
- 📝 Donor Registration
- 📅 Appointment Scheduling
- 📈 Reports Generation

## 🛠️ Technologies Used
- Java SE
- Swing GUI Framework
- MySQL Database
- Maven Build Tool

## 🚀 Getting Started

### Prerequisites
- ☕ Java JDK 8 or higher
- 📦 Maven 3.6 or higher
- 🗄️ MySQL 5.7 or higher

### Installation
1. Clone the repository
```bash
git clone https://github.com/VehanRajintha/-blood-donation-system.git
```

2. Navigate to project directory
```bash
cd -blood-donation-system
```

3. Install dependencies
```bash
mvn install
```

4. Configure database
- Create a MySQL database named `blood_donation_db`
- Import the `blood_donation_db.sql` file
- Update database credentials in `src/main/resources/database.properties`

5. Run the application
```bash
mvn exec:java -Dexec.mainClass="com.blooddonation.ui.LoginScreen"
```

## 📱 Usage
1. **Login/Register**: Start by creating an account or logging in
2. **Dashboard**: Access your personalized dashboard
3. **Donate**: Schedule blood donations
4. **Track**: Monitor donation history and status
5. **Reports**: Generate and view reports (Admin only)

## 🏗️ Project Structure
```
blood-donation-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/blooddonation/
│   │   │       ├── ui/
│   │   │       ├── model/
│   │   │       ├── dao/
│   │   │       └── util/
│   │   └── resources/
│   └── test/
├── database/
├── docs/
└── target/
```

## 🤝 Contributing
We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.

## 👥 Authors
- **Vehan Rajintha** - *Initial work*

## 🙏 Acknowledgments
- Thanks to all contributors who have helped shape this project
- Special thanks to the open source community
- Inspired by the need for efficient blood donation management

## 📞 Contact
- GitHub: [@VehanRajintha](https://github.com/VehanRajintha)

## 📊 Project Status
![GitHub stars](https://img.shields.io/github/stars/VehanRajintha/-blood-donation-system?style=social)
![GitHub forks](https://img.shields.io/github/forks/VehanRajintha/-blood-donation-system?style=social)
![GitHub issues](https://img.shields.io/github/issues/VehanRajintha/-blood-donation-system)
![GitHub license](https://img.shields.io/github/license/VehanRajintha/-blood-donation-system)

---
⭐ Star this repository if you find it helpful! 
