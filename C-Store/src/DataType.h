#ifndef _DATATYPE_H_
#define _DATATYPE_H_

#define MAX_NUM 128
#define PAGE_SIZE 32*1024
#define NUMBER_TO_SORT 100000

struct Record {
  double orderkey[1024];
  double custkey[1024];
  double totalprice[1024];
  double shippriority[1024];
};

struct Page {
  Record rec;
};

#endif  // _DATATYPE_H_
