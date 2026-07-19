<div align="center">

# 🌱 Spring Framework Full Course

### All Notes & Code — *Coder Army* YouTube Channel

*A complete, lecture-by-lecture Java Spring Boot learning repository*

[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![YouTube](https://img.shields.io/badge/YouTube-Coder%20Army-FF0000?style=for-the-badge&logo=youtube&logoColor=white)](https://www.youtube.com/@CoderArmy9)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

[![Stars](https://img.shields.io/github/stars/USERNAME/Spring-Framework-Full-Course?style=social)](../../stargazers)
[![Forks](https://img.shields.io/github/forks/USERNAME/Spring-Framework-Full-Course?style=social)](../../network/members)
[![Last Commit](https://img.shields.io/github/last-commit/USERNAME/Spring-Framework-Full-Course)](../../commits/main)

</div>

---

## 📌 About This Repository

This repository is a **structured archive of notes and hands-on code** for every lecture in the **Spring Framework & Spring Boot** series by **[Coder Army](https://www.youtube.com/@CoderArmy9)** on YouTube.

Each lecture has its own dedicated folder containing:

- 📝 **Notes** — concise, exam/interview-ready markdown notes for that lecture
- 💻 **Code** — the exact runnable project/snippets built during the video
- 🔗 **Video Link** — a direct link back to the corresponding YouTube lecture

The goal is to make this a **self-contained revision hub** — whether you're following the course live, revising before an interview, or referencing a specific Spring concept later.

> ⭐ If this repository helps you, consider giving it a star — it helps others discover it too.

---

## 🧭 Table of Contents

- [About This Repository](#-about-this-repository)
- [Repository Structure](#-repository-structure)
- [Lecture Index](#-lecture-index)
- [Tech Stack & Tools](#-tech-stack--tools)
- [Getting Started](#-getting-started)
- [How to Use This Repo](#-how-to-use-this-repo)
- [Progress Tracker](#-progress-tracker)
- [Contributing](#-contributing)
- [Credits](#-credits)
- [Connect With Me](#-connect-with-me)
- [License](#-license)

---

## 🗂 Repository Structure

```text
Spring-Framework-Full-Course/
│
├── Lecture 01/
│   ├── notes/
│   │   └── notes.md
│   └── code/
│       └── (source code for this lecture)
│
├── Lecture 02/
│   ├── notes/
│   └── code/
│
├── Lecture 03/
│   ├── notes/
│   └── code/
│
├── ...
│
└── README.md
```

Every lecture folder is **independent and self-runnable** — you don't need to set up the entire repo to try out a single lecture's code.

---

## 📚 Lecture Index

> Update the **Topic** and **Video** columns as new lectures are added. Keep the numbering in sync with your folder names for easy navigation.

| # | Topic | Notes | Code | Video |
|---|-------|:-----:|:----:|:-----:|
| 01 | Introduction to Spring & Spring Boot | [📄](./Lecture%2001/notes) | [💻](./Lecture%2001) | [▶️](https://youtube.com/@CoderArmy9) |
| 02 | IoC Container & Dependency Injection | [📄](./Lecture%2002/notes) | [💻](./Lecture%2002) | [▶️](https://youtube.com/@CoderArmy9) |
| 03 | Spring Beans, Scopes & Lifecycle | [📄](./Lecture%2003/notes) | [💻](./Lecture%2003) | [▶️](https://youtube.com/@CoderArmy9) |
| 04 | Spring Boot Annotations Deep Dive | [📄](./Lecture%2004/notes) | [💻](./Lecture%2004) | [▶️](https://youtube.com/@CoderArmy9) |
| 05 | Building REST APIs with Spring MVC | [📄](./Lecture%2005/notes) | [💻](./Lecture%2005) | [▶️](https://youtube.com/@CoderArmy9) |
| 06 | Spring Data JPA & Hibernate | [📄](./Lecture%2006/notes) | [💻](./Lecture%2006) | [▶️](https://youtube.com/@CoderArmy9) |
| 07 | Exception Handling & Validation | [📄](./Lecture%2007/notes) | [💻](./Lecture%2007) | [▶️](https://youtube.com/@CoderArmy9) |
| 08 | Spring Security Fundamentals | [📄](./Lecture%2008/notes) | [💻](./Lecture%2008) | [▶️](https://youtube.com/@CoderArmy9) |
| 09 | Working with Databases & Transactions | [📄](./Lecture%2009/notes) | [💻](./Lecture%2009) | [▶️](https://youtube.com/@CoderArmy9) |
| 10 | Testing Spring Boot Applications | [📄](./Lecture%2010/notes) | [💻](./Lecture%2010) | [▶️](https://youtube.com/@CoderArmy9) |

<details>
<summary><strong>➕ Add a new lecture row</strong></summary>

```md
| 11 | Your Topic Here | [📄](./Lecture%2011/notes) | [💻](./Lecture%2011) | [▶️](https://youtube.com/@CoderArmy9) |
```

</details>

---

## 🛠 Tech Stack & Tools

| Category | Tools |
|---|---|
| **Language** | Java 17+ |
| **Framework** | Spring Framework, Spring Boot 3.x |
| **Build Tool** | Maven |
| **Database** | MySQL / PostgreSQL / H2 (per lecture) |
| **ORM** | Hibernate / Spring Data JPA |
| **API Testing** | Postman |
| **IDE** | IntelliJ IDEA |
| **Version Control** | Git & GitHub |

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher installed (`java -version`)
- Maven installed (`mvn -version`)
- An IDE (IntelliJ IDEA recommended)
- Git installed on your machine

### Clone the Repository

```bash
git clone https://github.com/USERNAME/Spring-Framework-Full-Course.git
cd Spring-Framework-Full-Course
```

### Run a Specific Lecture's Project

```bash
cd "Lecture 05"
mvn clean install
mvn spring-boot:run
```

Each lecture folder that contains a Maven project can be opened independently in your IDE as its own module.

---

## 📖 How to Use This Repo

1. **Follow along with the video** — open the matching `Lecture XX` folder while watching.
2. **Read the notes first** — each `notes/` folder has a concise write-up of the concept before you dive into code.
3. **Run the code yourself** — don't just read it; execute it, break it, and fix it.
4. **Revise later** — use the [Lecture Index](#-lecture-index) as a quick-reference sheet before interviews.

---

## ✅ Progress Tracker

Track your own learning progress as the series continues:

- [x] Lecture 01 — Introduction to Spring & Spring Boot
- [x] Lecture 02 — IoC Container & Dependency Injection
- [x] Lecture 03 — Spring Beans, Scopes & Lifecycle
- [ ] Lecture 04 — Spring Boot Annotations Deep Dive
- [ ] Lecture 05 — Building REST APIs with Spring MVC
- [ ] Lecture 06 — Spring Data JPA & Hibernate
- [ ] Lecture 07 — Exception Handling & Validation
- [ ] Lecture 08 — Spring Security Fundamentals
- [ ] Lecture 09 — Working with Databases & Transactions
- [ ] Lecture 10 — Testing Spring Boot Applications

---

## 🤝 Contributing

Contributions are welcome! If you spot an error in the notes, want to add better explanations, or improve any code sample:

1. Fork this repository
2. Create a new branch (`git checkout -b fix/lecture-05-notes`)
3. Commit your changes (`git commit -m "Fix: corrected bean scope explanation in Lecture 05"`)
4. Push to your branch (`git push origin fix/lecture-05-notes`)
5. Open a Pull Request

Please keep the existing folder structure (`Lecture XX/notes` and `Lecture XX/code`) when contributing.

---

## 🙏 Credits

All conceptual content is based on the **Spring Framework & Spring Boot** series taught by **[Coder Army](https://www.youtube.com/@CoderArmy9)** on YouTube. This repository only compiles personal notes and practice code created while following the course — full credit for the teaching and curriculum goes to the original creator.

---

## 📬 Connect With Me

<div align="left">

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/USERNAME)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/USERNAME)
[![LeetCode](https://img.shields.io/badge/LeetCode-FFA116?style=for-the-badge&logo=leetcode&logoColor=black)](https://leetcode.com/u/Soumen827/)

</div>

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) — feel free to use these notes for your own learning journey.

<div align="center">

**⭐ Star this repo if it helped you learn Spring Boot faster!**

</div>
