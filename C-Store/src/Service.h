#ifndef _QUERY_H_
#define _QUERY_H_

#include <string.h>
#include <fstream>
#include "DataType.h"
#include "BufferManager.h"
#include "ExternalSort.h"

class Service {
 public:
  Service();
  bool load(std::string tableName);
  void retrieve(std::string tableName, double primarykey);
  float compress(std::string tableName, std::string column);
  void join();
  int count(std::string tableName);
  double getFileSize(char *fileName);
  char* columnName(std::string col);
  const char *loadFileName(std::string tableName);
  const char *builtFileName(std::string property);
  ~Service();
 private:
  BufferManager* buf;
};

#endif  // _QUERY_H_
