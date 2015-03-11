#ifndef _BUFFERMANAGER_H_
#define _BUFFERMANAGER_H_

#include <fstream>
#include "DataType.h"

class BufferManager {
 public:
  BufferManager();
  int readData();
  int getPagenum();
  Page* getPage();
  void closeFile();
  ~BufferManager();
 private:
  FILE *order, *cust, *price, *priority;
  Page *page;
  int pagenum;
};

#endif  // _BUFFERMANAGER_H_
