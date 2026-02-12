# Bank Transfer Service - Ćwiczenia Git dla Politechniki Gdańskiej

Projekt edukacyjny do nauki **Kontroli Wersji Git** na studiach podyplomowych Politechniki Gdańskiej.

## 📚 O Projekcie

Jest to uproszczona aplikacja bankowa implementująca system transferów pieniędzy między kontami. Projekt zawiera:

- ✅ Logikę biznesową (transfer pieniędzy, walidacja IBAN, limity)
- ✅ Testy JUnit 5 z Mockito (pełne pokrycie)
- ✅ Strukturę Maven + Spring Boot
- ✅ Błędy do naprawy przez studentów na branchu `develop` w klasie testowej TransferServiceBuggyTest

## 🚀 Quick Start

### 1. Klonowanie Repozytorium

```bash
git clone https://github.com/rejkom/git-learning-bank-transfer.git
cd bank-transfer-service
```

### 2. Instalacja Zależności

```bash
mvn clean install
```

### 3. Uruchomienie Testów

```bash
mvn test
```

### 4. Struktura Projektu

```
src/
├── main/java/com/bankservice/
│   ├── model/              # Modele danych (Account, Transfer, TransferStatus)
│   ├── repository/         # Dostęp do danych (AccountRepository)
│   ├── service/            # Logika biznesowa (TransferService)
│   └── exception/          # Custom exceptions
└── test/java/com/bankservice/
    └── service/            # Testy JUnit 5 + Mockito
```

## 📋 Scenariusze Ćwiczeń Git

### Scenariusz 1: Klonowanie i Setup

**Cel:** Zaznajomienie się z podstawowymi poleceniami Git

```bash
# 1. Sklonuj repozytorium
git clone https://github.com/rejkom/git-learning-bank-transfer.git

# 2. Przejdź do folderu
cd bank-transfer-service

# 3. Wyświetl gałęzie
git branch -a

# 4. Sprawdź status
git status

# 5. Wyświetl historię commitów na branchu master
git log --oneline -n 5
```

### Scenariusz 2: Praca na Feature Branchu

**Cel:** Ćwiczenie tworzenia branchy i commitów

```bash
# 1. Upewnij się, że jesteś na branchu master
git checkout master

# 2. Uaktualnij kod z repozytorium zdalnego
git pull origin master

# 3. Stwórz nowy feature branch
git checkout -b feature/your-feature-name

# 4. Edytuj plik (np. dodaj nową metodę)
nano src/main/java/com/bankservice/service/TransferService.java

# 5. Sprawdź co się zmieniło
git diff

# 6. Dodaj zmiany do staging area
git add src/main/java/com/bankservice/service/TransferService.java

# 7. Zatwierdź zmianę
git commit -m "feat: add new transfer validation method"

# 8. Wyślij do repozytorium zdalnego
git push origin feature/your-feature-name
```

### Scenariusz 3: Naprawianie Błędów (Zaliczenie)

**Cel:** Znalezienie i naprawienie błędów w klasie testowej na branchu `develop`

1. Przełącz się na branch `develop`:
   ```bash
   git checkout develop
   git pull origin develop
   ```

2. Uruchom testy, aby zobaczyć które są czerwone:
   ```bash
   mvn clean test
   ```

3. Przejrzyj błędy i napraw kod pliku testowym:
   - `src/test/java/com/bankservice/service/TransferServiceBuggyTest.java`

4. Po naprawie, zatwierdź swoje zmiany:
   ```bash
   git add .
   git commit -m "Twój message po naprawieniu testów do tego commita"
   ```

5. Wyślij swoje zmiany:
   ```bash
   git push origin develop
   ```

### Scenariusz 4: Dodatkowe, nieobowiązkowe ćwiczenie na zaliczenie

1. Przejdź na branch `develop` i utwórz nowy branch
2. Dodaj plik tekstowy w katalogu `resources` gdzie już znajduje się `example.file.txt`
3. Dodaj etykiete 'opisaną'
4. Wyślij zmiany do zdalnego repozytorium (pamiętaj o etykiecie)
5. Przejdź na branch `develop` i zmerguj swój branch
6. Upewnij się, że zmiany są widoczne na branchu `develop` na zdalnych repozytorium w GitHub

## 📊 Ćwiczenia do Wykonania na Zajęciach

### Ćwiczenie 1: Przeglądanie Historii
```bash
# Pokaż wszystkie commity na branchu master
git log --oneline

# Pokaż commity z danymi autora i datą
git log --pretty=fuller

# Pokaż tylko ostatnie 3 commity
git log -n 3
```

### Ćwiczenie 2: Gałęzie (Branches)
```bash
# Lista wszystkich lokalnych gałęzi
git branch

# Lista wszystkich gałęzi (lokalne + zdalne)
git branch -a

# Utwórz nową gałąź
git checkout -b feature/new-feature
# lub: git switch -c feature/new-feature

# Przełącz się na inną gałąź
git checkout master

# Usuń gałąź (lokalnie)
git branch -d feature/new-feature
```

### Ćwiczenie 3: Staging Area
```bash
# Dodaj konkretny plik
git add src/main/java/com/bankservice/model/Account.java

# Dodaj wszystkie zmienione pliki
git add .

# Pokaż jakie zmiany są w staging area
git diff --cached

# Cofnij zmianę z staging area (bez usuwania zmian w plikach)
git reset HEAD src/main/java/com/bankservice/model/Account.java
```

### Ćwiczenie 4: Commits
```bash
# Utwórz commit z wiadomością
git commit -m "feat: add account validation"

# Zmień ostatni commit (jeśli nie został push'owany)
git commit --amend -m "feat: add account and balance validation"

# Pokaż szczegóły ostatniego commitu
git show
```

### Ćwiczenie 5: Push i Pull
```bash
# Pobierz najnowsze zmiany ze zdalnego branchu
git pull origin master

# Wyślij zmiany do zdalnego branchu
git push origin feature/my-feature

# Wyślij wszystkie lokalne gałęzie
git push -u origin feature/my-feature
```

## 📝 Struktura Commitów - Best Practices

Przykładowe sformułowania wiadomości commit:

```
feat: add new account validation
^--^  ^-----------------------^
|     |
|     `-> Opis zmian (imperatyw, mała litera na początek)
|
`-> Typ: feat, fix, docs, style, refactor, test, chore
```

Przykłady:
- ✅ `feat: add transfer amount validation`
- ✅ `fix: correct IBAN validation logic`
- ✅ `test: add edge case tests for insufficient funds`
- ❌ `fixed the thing` (zbyt niejasny)
- ❌ `Updated code` (zbyt generycznie)

## 🔧 Troubleshooting

### Problem: "Conflicts on merge"
Jeśli napotkasz konflikt przy merge'owaniu, musisz ręcznie edytować plik i wybrać którą wersję chcesz zachować.

### Problem: "Commit with wrong message"
```bash
# Zmień ostatni commit (jeśli nie został push'owany)
git commit --amend -m "correct message"
```

### Problem: "Need to undo last commit"
```bash
# Cofnij ostatni commit, ale zachowaj zmiany
git reset --soft HEAD~1

# Cofnij ostatni commit i usuń zmiany
git reset --hard HEAD~1
```

## 📚 Materiały Dodatkowe

- [Git Pro Book - PL](https://git-scm.com/book/pl/v2)
- [Interactive Git Tutorial](https://learngitbranching.js.org/)
- [GitHub Guides](https://guides.github.com/)

## 👨‍💼 Informacje o Projekcie

- **Autor:** Politechnika Gdańska - Studia Podyplomowe
- **Cel:** Nauka Git Version Control
- **Język:** Java 21 + Maven
- **Framework:** Spring Boot 4
- **Testing:** JUnit 5 + Mockito

---

**Powodzenia w ćwiczeniach! 🚀**