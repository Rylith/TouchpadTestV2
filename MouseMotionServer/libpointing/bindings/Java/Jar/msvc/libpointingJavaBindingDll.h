// Le bloc ifdef suivant est la fa�on standard de cr�er des macros qui facilitent l'exportation 
// � partir d'une DLL. Tous les fichiers contenus dans cette DLL sont compil�s avec le symbole LIBPOINTINGJAVABINDINGDLL_EXPORTS
// d�fini sur la ligne de commande. Ce symbole ne doit pas �tre d�fini dans les projets
// qui utilisent cette DLL. De cette mani�re, les autres projets dont les fichiers sources comprennent ce fichier consid�rent les fonctions 
// LIBPOINTINGJAVABINDINGDLL_API comme �tant import�es � partir d'une DLL, tandis que cette DLL consid�re les symboles
// d�finis avec cette macro comme �tant export�s.
#ifdef LIBPOINTINGJAVABINDINGDLL_EXPORTS
#define LIBPOINTINGJAVABINDINGDLL_API __declspec(dllexport)
#else
#define LIBPOINTINGJAVABINDINGDLL_API __declspec(dllimport)
#endif

// Cette classe est export�e de libpointingJavaBindingDll.dll
class LIBPOINTINGJAVABINDINGDLL_API ClibpointingJavaBindingDll {
public:
	ClibpointingJavaBindingDll(void);
	// TODO�: ajoutez ici vos m�thodes.
};

extern LIBPOINTINGJAVABINDINGDLL_API int nlibpointingJavaBindingDll;

LIBPOINTINGJAVABINDINGDLL_API int fnlibpointingJavaBindingDll(void);
