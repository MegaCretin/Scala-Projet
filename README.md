Groupe: 
    DEBORDE Clément
    MEGHARBA Bruno
    ESTREMS Arthur

Méthode de travail, Pairs programming 

Lancer le programme:
* Compiler le fichier avec la commande: scalac Main.scala
* Exécuter le programme avec la commande : scala Main

Les differentes commandes:
- exit - Quitte le programme
    * Parametre: Aucun
- dummy - Créer une canvas de taille 3x4
    * Parametre: Aucun
- dummy2 - Créer une canvas de taille 3x1
    * Parametre: Aucun
- new_canvas - Créer une canvas de la taille rentrée en paramêtre 
    * Parametre: 3 -> largeur et hauteur (width et height) et la couleur (symbole)
    * Exemple: new_canvas 2 1 #
- load_image - Créer un canvas à partir d'un image rentrée en paramêtre 
    * Parametre: 1 -> Le nom du fichier (ici triforce)
    * Exemple: load_image triforce
- update_pixel - Permet de changer la couleur d'un pixel choisi du canvas
    * Parametre: 2 -> coordonnee et la nouvelle couleur (symbole)
    * Exemple: update_pixel 2,2 #
- draw - Permet de dessiner une forme 
    * line - Dessine une ligne
        # Parametre: 3 -> coordonnee du pixel1, coordonnee du pixel2 et la couleur (symbole)
        # Exemple: draw line 2,2 4,2 #" 
    * rectangle - Dessine un rectangle
        # Parametre: 3 -> coordonnee du pixel1, coordonnee du pixel2 et la couleur (symbole)
        # Exemple: draw rectangle 2,2 4,4 #" 
    * fill - Rempli une forme
        # Parametre: 2 -> coordonnee du pixel et la couleur (symbole)
        # Exemple: draw fill x,y ." 
    * triangle - Dessine un triangle
        # Parametre: 3 -> coordonnee du coin1, coordonnee du coin2, coordonnee du coin3 et la couleur (symbole)
        # Exemple: draw triangle x1,y1 x2,y2 x3,y3 ." 
    * polygon - Dessine un polygon
        # Parametre: infiny -> coordonnee de tous les coins et la couleur (symbole)
        # Exemple: draw polygon 2,2 4,2 5,8 #" 