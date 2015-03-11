#include "Function.h"

Function::Function() {}
Function::~Function() {}

void Function::qucikSort(double array1[], double array2[], int start, int end) {
  int i = start;
  int j = end;
  double x = array1[(i+j)/2];
  do {
    while (x > array1[i]) i++;
    while (x < array1[j]) j--;
    if (i <= j) {
      swap(array1[i], array1[j]);
      swap(array2[i], array2[j]);
      i++;
      j--;
    }
  } while (i <= j);
  if (i < end) qucikSort(array1, array2, i, end);
  if (start < j) qucikSort(array1, array2, start, j);
}

void Function::swap(double &a, double &b) {
  double temp = a;
  a = b;
  b = temp;
}
