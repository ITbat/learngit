#ifndef _UI_H_
#define _UI_H_

#include "Service.h"

class UI {
 public:
  UI();
  void work(int a, char* arg[]);
  ~UI();
 private:
  Service service;
};

#endif  // _UI_H_
