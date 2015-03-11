#include "ExternalSort.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <ctime>
#include <fstream>
#include <cassert>
#include <iostream>
#include "Function.h"

ExternalSort::ExternalSort(char *input_file, char *out_file, int count) {
  m_count = count;
  m_in_file = sort_FileName(input_file);
  m_out_file = sorted_FileName(out_file);
}

ExternalSort::~ExternalSort() {
  delete [] m_in_file;
  delete [] m_out_file;
}

char* ExternalSort::sort() {
  // sort the data of the file in blocks, and then written to temp files
  int file_count = memory_sort();
  // merge the temp files to the out-file
  merge_sort(file_count);
  return m_out_file;
}

int ExternalSort::read_data(FILE* cust, FILE* order, double a_cust[],
  double a_order[], int n) {
  int counter = 0;
  if (n > 0) {
    counter = fread(a_cust, sizeof(double), n, cust);
    fread(a_order, sizeof(double), counter, order);
  }
  return counter;
}

void ExternalSort::write_data(FILE* cust, FILE* order, double a_cust[],
  double a_order[], int n) {
  if (n > 0) {
    fwrite(a_cust, sizeof(double), n, cust);
    fwrite(a_order, sizeof(double), n, order);
  }
}

char* ExternalSort::sort_FileName(char *tableName) {
  char *sortFile = new char[100];
  sprintf(sortFile, "%s.bin", tableName);
  return sortFile;
}

char* ExternalSort::sorted_FileName(char *tableName) {
  char *sortedFile = new char[100];
  sprintf(sortedFile, "Esorted_%s.bin", tableName);
  return sortedFile;
}

char* ExternalSort::temp_filename(int index) {
  char *tempfile = new char[100];
  sprintf(tempfile, "temp%d", index);
  return tempfile;
}

char* ExternalSort::o_temp_filename(int index) {
  char *o_tempfile = new char[100];
  sprintf(o_tempfile, "o_temp%d", index);
  return o_tempfile;
}

int ExternalSort::memory_sort() {
  FILE* fin = fopen(m_in_file, "rb");
  FILE* order = fopen("orderkey.bin", "rb");

  if (order == NULL) {
    std::cout << "Can not find the orderkey.bin, please 'load orders' first"
      << std::endl;
    return -1;
  }

  int n = 0, file_count = 0, o_file_count = 0;
  double *array = new double[m_count];
  double *a_order = new double[m_count];

  // read m_count data and take a qucik-sort, then written to temp files
  while ((n = read_data(fin, order, array, a_order, m_count)) > 0) {
    f.qucikSort(array, a_order, 0, n-1);

    char *fileName1 = temp_filename(file_count++);
    char *fileName2 = o_temp_filename(o_file_count++);

    FILE *tempFile1 = fopen(fileName1, "wb");
    free(fileName1);
    FILE *tempFile2 = fopen(fileName2, "wb");
    free(fileName2);

    write_data(tempFile1, tempFile2, array, a_order, n);
    fclose(tempFile1);
    fclose(tempFile2);
  }

  delete [] array;
  delete [] a_order;

  fclose(fin);
  fclose(order);

  return file_count;
}

void ExternalSort::merge_sort(int file_count) {
  if (file_count <= 0) return;

  FILE *fout = fopen(m_out_file, "wb");
  FILE *o_fout = fopen("Esorted_orderkey.bin", "wb");

  FILE* *farray = new FILE*[file_count];
  FILE* *o_farray = new FILE*[file_count];

  int i;

  for (i = 0; i < file_count; ++i) {
    char* fileName = temp_filename(i);
    char* o_fileName = o_temp_filename(i);

    farray[i] = fopen(fileName, "rb");
    o_farray[i] = fopen(o_fileName, "rb");

    free(fileName);
    free(o_fileName);
  }

  double *data = new double[file_count];  // storing a data of each temp file
  double *o_data = new double[file_count];
  bool *hasNext = new bool[file_count];  // a sign whether the file has been read completely
  memset(data, 0, sizeof(double) * file_count);
  memset(o_data, 0, sizeof(double) * file_count);
  memset(hasNext, 1, sizeof(bool) * file_count);

  // initial read
  for (i = 0; i < file_count; ++i) {
    if (!fread(&data[i], sizeof(double), 1, farray[i]))  // read the first data of each file to array data
      hasNext[i] = false;
    fread(&o_data[i], sizeof(double), 1, o_farray[i]);
  }

  // cycle read and write，the way to select the minimum number is simple traversal selection method
  while (true) {
    /* find the minimum and available number in
     * data, and recorded the index of corresponding file */
    int j = 0;

    // skip the files have been read completely, and remove them
    while (j < file_count && !hasNext[j]) {
      char *tempFile = new char[20];
      char *o_tempFile = new char[20];
      sprintf(tempFile, "temp%d", j);
      sprintf(o_tempFile, "o_temp%d", j);
      remove(tempFile);
      remove(o_tempFile);
      j++;
    }

    double min = data[j];
    double o_dd = o_data[j];

    if (j >= file_count)  // stop the merge
      break;

    // chose the minimum number
    for (i = j+1; i < file_count; ++i) {
      if (hasNext[i] && min > data[i]) {
        min = data[i];
        o_dd = o_data[i];
        j = i;
      }
    }

    // read the next data of file
    if (!fread(&data[j], sizeof(double), 1, farray[j]))
      hasNext[j] = false;
    fread(&o_data[j], sizeof(double), 1, o_farray[j]);

    fwrite(&min, sizeof(double), 1, fout);
    fwrite(&o_dd, sizeof(double), 1, o_fout);
  }

  delete [] hasNext;
  delete [] data;
  delete [] o_data;

  for (i = 0; i < file_count; ++i) {
    fclose(farray[i]);
    fclose(o_farray[i]);
  }

  delete [] farray;
  delete [] o_farray;

  fclose(fout);
  fclose(o_fout);
}
