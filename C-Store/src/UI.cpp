#include "UI.h"
#include <stdlib.h>
#include <iomanip>
#include <string>
#include <iostream>

UI::UI() {}

UI::~UI() {}

void UI::work(int a, char* arg[]) {
  if (strcmp(arg[1], "load") == 0) {
    std::cout << "Loading" << " " << arg[2] << "..." << std::endl;
    if (!service.load(arg[2]))
      std::cout << "load failed" << std::endl;
  } else if (strcmp(arg[1], "retrieve") == 0) {
           if (strcmp(arg[2], "orders") == 0) {
             FILE* fp = fopen("orderkey.bin", "rb");
             if (!fp) {
               std::cout << "Can not find the targe file, please load first"
                 << std::endl;
             } else {
                 double primarykey;

                 std::cout << "-----------------------------------------" << '\n'
                   << "                Retrieve                 " << '\n'
                   << "Enter primarykey to get retrieve result  " << '\n'
                   << "Enter Ctrl+D to exit                     " << '\n'
                   << "-----------------------------------------" << std::endl;

               while (scanf("%lf", &primarykey) != EOF)
                 service.retrieve(arg[2], primarykey);
             }

             if (fp)
               fclose(fp);
           } else {
               std::cout << "Can not find the targe file, please load first"
                 << std::endl;
             }
         } else if (strcmp(arg[1], "compress") == 0) {
                  double efficiency = service.compress(arg[2], arg[3]);
                  if (efficiency == -1)
                    std::cout << "Can not find the targe file, please load first"
                      << std::endl;
                  else
                    std::cout << "Compression efficiency: " << efficiency << "%"
                      << std::endl;
                } else if (strcmp(arg[1], "join") == 0) {
                         service.join();
                       } else if (strcmp(arg[1], "count") == 0) {
                                if (int totalNumber = service.count(""))
                                  std::cout << "Row Number of table orders: "
                                    << totalNumber << std::endl;
                                else
                                  std::cout << "Can not find the file"
                                    << std::endl;
                              } else {
                                  std::cout << arg[1] << ": command not found"
                                    << std::endl;
                                }
}
