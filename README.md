# TP1 – Java RMI : Cabinet Vétérinaire Distribué

> **Module** : Architecture Logicielle Distribuée (HAI704I)  
> **Technologie** : Java RMI (Remote Method Invocation)

---

## Table des matières

1. [Présentation](#présentation)
2. [Architecture du projet](#architecture-du-projet)
3. [Prérequis](#prérequis)
4. [Compilation](#compilation)
5. [Lancement](#lancement)
6. [Système d'alertes (Observer RMI)](#système-dalertes-observer-rmi)
7. [Utilisation du client (menu console)](#utilisation-du-client-menu-console)
8. [Commandes Git](#commandes-git)
9. [Structure des packages](#structure-des-packages)

---

## Présentation

Ce TP implémente un **cabinet vétérinaire distribué** à l'aide de Java RMI.  
Un serveur expose un objet distant `Cabinet` dans un registre RMI. Des clients distants s'y connectent pour :

- Lister, rechercher, ajouter et supprimer des patients (animaux).
- Consulter et modifier le dossier médical d'un patient.
- **S'abonner à un système d'alertes** basé sur le patron Observer : le serveur notifie en temps réel chaque client abonné lorsque le nombre de patients franchit des seuils prédéfinis (100, 500, 1000).

---

## Architecture du projet

```
TP1-RMI/
├── common/          # Interfaces RMI & classes sérialisables partagées
│   └── src/animal/common/
│       ├── Animal.java                      # Interface distante : animal
│       ├── Cabinet.java                     # Interface distante : cabinet
│       ├── AlerteObserver.java              # Interface distante : observateur d'alertes
│       ├── Dossier.java                     # Interface distante : dossier médical
│       ├── Espece.java                      # Classe sérialisable
│       ├── Chien.java
│       ├── PatientDejaExistantException.java
│       └── PatientIntrouvableException.java
│
├── server/          # Implémentations serveur (UnicastRemoteObject)
│   └── src/animal/server/
│       ├── Server.java                      # Point d'entrée serveur
│       ├── CabinetImpl.java                 # Gestion des patients + diffusion des alertes
│       ├── AnimalImpl.java                  # Implémentation d'un animal
│       └── DossierImpl.java                 # Implémentation d'un dossier
│
└── client/          # Client console
    └── src/animal/client/
        ├── ClientMain.java                  # Point d'entrée client  [hote] [port]
        ├── ClientLogic.java                 # Couche métier : appels RMI
        ├── ConsoleUI.java                   # Interface console interactive
        ├── AlerteObserverImpl.java          # Servant hébergé côté client (callbacks)
        ├── PatientVue.java                  # DTO d'affichage
        └── ClientException.java            # Exception métier client
```

---

## Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java JDK | 17+ |
| PowerShell | 5.1+ (Windows) |

Vérifiez votre installation :

```powershell
java -version
javac -version
```

---

## Compilation

> **Toutes les commandes sont à exécuter depuis la racine du projet** (`TP1-RMI/`).

### 1. Nettoyer les anciens binaires

```powershell
Remove-Item -Recurse -Force common\out, server\out, client\out -ErrorAction SilentlyContinue
```

### 2. Compiler le module `common` (interfaces & classes partagées)

```powershell
javac -d common\out (Get-ChildItem -Recurse -Filter *.java common\src).FullName
```

### 3. Compiler le module `server`

```powershell
javac -cp common\out -d server\out (Get-ChildItem -Recurse -Filter *.java server\src).FullName
```

### 4. Compiler le module `client`

```powershell
javac -cp common\out -d client\out (Get-ChildItem -Recurse -Filter *.java client\src).FullName
```

### Compilation complète en une seule séquence

```powershell
Remove-Item -Recurse -Force common\out, server\out, client\out -ErrorAction SilentlyContinue
javac -d common\out (Get-ChildItem -Recurse -Filter *.java common\src).FullName
javac -cp common\out -d server\out (Get-ChildItem -Recurse -Filter *.java server\src).FullName
javac -cp common\out -d client\out (Get-ChildItem -Recurse -Filter *.java client\src).FullName
```

---

## Lancement

### Terminal 1 – Démarrer le serveur

Le serveur crée automatiquement le registre RMI sur le port **1099** et y publie l'objet `Cabinet`.

```powershell
java -cp "common\out;server\out" animal.server.Server
```

Sortie attendue :
```
RMI Registry started on port 1099.
Server is ready and 'Cabinet' is published.
```

### Terminal 2 – Démarrer le client (connexion locale)

```powershell
java -cp "common\out;client\out" animal.client.ClientMain
```

### Client sur une machine distante

```powershell
java -cp "common\out;client\out" animal.client.ClientMain <hote> [port]
```

| Argument | Description | Valeur par défaut |
|----------|-------------|------------------|
| `hote` | Adresse IP ou nom d'hôte du serveur | `localhost` |
| `port` | Port du registre RMI | `1099` |

**Exemples :**

```powershell
# Connexion à un serveur distant sur le port par défaut
java -cp "common\out;client\out" animal.client.ClientMain 192.168.1.42

# Connexion à un port personnalisé
java -cp "common\out;client\out" animal.client.ClientMain 192.168.1.42 2000
```

---

## Système d'alertes (Observer RMI)

### Principe

Le système d'alertes implémente le **patron Observer distribué** via RMI :

- Le **serveur** (`CabinetImpl`) maintient une liste d'observateurs distants (`AlerteObserver`).
- Le **client** exporte un servant `AlerteObserverImpl` (objet RMI hébergé côté client).
- Lorsque le nombre de patients franchit un **seuil** (à la hausse ou à la baisse), le serveur appelle `notifier()` sur chaque observateur abonné — c'est un **callback RMI** : le serveur rappelle le client.

### Seuils configurés (dans `CabinetImpl`)

| Seuil | Déclenchement |
|-------|--------------|
| **100** patients | Franchissement à la hausse ou à la baisse |
| **500** patients | Franchissement à la hausse ou à la baisse |
| **1000** patients | Franchissement à la hausse ou à la baisse |

### Format du message d'alerte

```
[ALERTE] Seuil 100 franchi à la hausse : 100 patients
[ALERTE] Seuil 100 franchi à la baisse : 99 patients
```

### Cycle de vie de l'abonnement

```
Client                              Serveur
  |                                    |
  |-- new AlerteObserverImpl() ------> | (export RMI côté client)
  |-- cabinet.abonner(stub) ---------> | (le stub est stocké dans observers)
  |                                    |
  |      [ajout/retrait de patient]    |
  |<-- observer.notifier(...) -------- | (callback du serveur vers le client)
  |                                    |
  |-- cabinet.desabonner(stub) ------> |
  |-- unexportObject(obs) -----------> | (libère le thread RMI côté client)
```

### S'abonner / se désabonner depuis le menu client

Dans le menu interactif du client, choisissez :

| Option | Action |
|--------|--------|
| `6` | **S'abonner aux alertes** – exporte l'observateur et s'enregistre auprès du serveur |
| `7` | **Se désabonner des alertes** – se désenregistre et désexporte l'observateur |
| `0` | **Quitter** – se désabonne automatiquement si abonné, puis ferme proprement |

> **Note** : le client héberge lui-même un mini-serveur RMI pour recevoir les callbacks. Si un client abonné devient injoignable, le serveur le retire automatiquement de la liste des observateurs lors du prochain envoi d'alerte.

### Tester le système d'alertes manuellement

Pour déclencher une alerte sur le seuil 100 :

1. Démarrez le serveur et le client.
2. Dans le client, choisissez **6** pour s'abonner.
3. Ajoutez des patients (option **3**) jusqu'à atteindre 100, 500 ou 1000.
4. L'alerte s'affiche en temps réel dans la console du client abonné, même si celui-ci est en train de saisir une commande.

---

## Utilisation du client (menu console)

```
=== Cabinet vétérinaire ===

--- Menu (alertes : non abonné) ---
1. Lister les patients
2. Rechercher un patient par nom
3. Enregistrer un nouveau patient
4. Consulter le dossier d'un patient
5. Mettre à jour le dossier d'un patient
6. S'abonner aux alertes
7. Se désabonner des alertes
0. Quitter
```

Lors de l'**enregistrement d'un patient** (option 3), le client demande :
- Nom de l'animal
- Nom du maître
- Race
- Espèce (nom + espérance de vie en années)
- Texte du dossier médical

---

## Commandes Git

```bash
# Ajouter tous les changements
git add .

# Commiter avec un message
git commit -m "votre message"

# Créer un tag de checkpoint
git tag cp-E

# Pousser vers le dépôt distant avec les tags
git push origin main --tags
```

---

## Structure des packages

| Package | Rôle |
|---------|------|
| `animal.common` | Interfaces RMI & classes sérialisables — partagés entre client et serveur |
| `animal.server` | Implémentations des objets distants & point d'entrée du serveur |
| `animal.client` | Logique client, UI console & observateur d'alertes côté client |
