#ifndef _EXTERNALSORT_H_
#define _EXTERNALSORT_H_

#include <stdio.h>
#include "Function.h"

class ExternalSort {
 public:
  ExternalSort(char *input_file, char *out_file, int count);
  virtual ~ExternalSort();
  char* sort();
 private:
  int m_count;       // the length of array
  char *m_in_file;   // the directory of in-file
  char *m_out_file;  // the directory of out-file
  Function f;
 protected:
  int read_data(FILE* cust, FILE* order,
    double a_cust[], double a_order[], int n);
  void write_data(FILE* cust, FILE* order,
    double a_cust[], double a_order[], int n);
  char* sort_FileName(char *tableName);
  char* sorted_FileName(char *tableName);
  char* temp_filename(int index);
  char* o_temp_filename(int index);
  int memory_sort();
  void merge_sort(int file_count);
};

#endif  // _EXTERNALSORT_H_
