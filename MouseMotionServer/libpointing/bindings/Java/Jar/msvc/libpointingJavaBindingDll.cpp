// libpointingJavaBindingDll.cpp�: d�finit les fonctions export�es pour l'application DLL.
//

#include "stdafx.h"
#include "libpointingJavaBindingDll.h"


// Il s'agit d'un exemple de variable export�e
LIBPOINTINGJAVABINDINGDLL_API int nlibpointingJavaBindingDll=0;

// Il s'agit d'un exemple de fonction export�e.
LIBPOINTINGJAVABINDINGDLL_API int fnlibpointingJavaBindingDll(void)
{
	return 42;
}

// Il s'agit du constructeur d'une classe qui a �t� export�e.
// consultez libpointingJavaBindingDll.h pour la d�finition de la classe
ClibpointingJavaBindingDll::ClibpointingJavaBindingDll()
{
	return;
}
