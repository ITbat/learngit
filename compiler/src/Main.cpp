#include <iostream>
#include <cstdlib>
#include <iomanip>
#include <fstream>
#include "Translator.h"
using namespace std;

int main(int argc, char *argv[]) {
	FILE *fp;
	char *fileName;
	if (argc != 2) {
		fprintf(stderr, "usage: %s <filename>\n", argv[0]);
		return 0;
	}
	fileName = argv[1];
	if ((fp = fopen(fileName, "r")) != NULL) {
		cout << "\n********************Result*********************" << endl;
		Translator t(fp);
		while (true) {
			t.execute();
		}
		fclose(fp);
	} else {
		cout << "Can not open the file" << endl;
	}
	return 0;
}