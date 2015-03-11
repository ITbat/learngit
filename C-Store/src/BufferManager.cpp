#include <string.h>
#include "BufferManager.h"

BufferManager::BufferManager() {
  order = fopen("orderkey.bin", "rb");
  cust = fopen("custkey.bin", "rb");
  price = fopen("totalprice.bin", "rb");
  priority = fopen("shippriority.bin", "rb");
  page = new Page[128];
  pagenum = 0;
}

BufferManager::~BufferManager() {}

int BufferManager::getPagenum() {
  return pagenum;
}

Page* BufferManager::getPage() {
  return page;
}

void BufferManager::closeFile() {
  fclose(order);
  fclose(cust);
  fclose(price);
  fclose(priority);
}

// read 128 pages every time
int BufferManager::readData() {
  int dataPage = 0;  // the data number of the last page
  Record rec;

  if (!order || !cust || !price || !priority)
    return -1;

  int i;
  for (i = 0; i < MAX_NUM; i++) {
    if (!fread(rec.orderkey, sizeof(double), 1024, order)) {
      pagenum = i;
      break;
    } else {
        dataPage = fread(rec.custkey, sizeof(double), 1024, cust);
        fread(rec.totalprice, sizeof(double), 1024, price);
        fread(rec.shippriority, sizeof(double), 1024, priority);
        memcpy(&page[i], &rec, sizeof(Page));
      }
  }
  pagenum = i;

  return dataPage;
}
