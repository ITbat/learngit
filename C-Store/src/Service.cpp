#include "Service.h"
#include <stdlib.h>
#include <iomanip>
#include <iostream>

Service::Service() {}

Service::~Service() {}

bool Service::load(std::string tableName) {
  time_t start = clock();

  std::string line;
  int count = 0;
  bool flag = false;

  if (tableName == "orders") {
      Page page;
      std::ifstream infile;
      const char* filename = loadFileName(tableName);
      infile.open(filename);

      FILE *order, *cust, *price, *priority;
      order = fopen(builtFileName("orderkey"), "wb");
      cust = fopen(builtFileName("custkey"), "wb");
      price = fopen(builtFileName("totalprice"), "wb");
      priority = fopen(builtFileName("shippriority"), "wb");

      char temp_order[20];
      char temp_cust[20];
      char temp_price[20];
      char temp_priority[20];

      while (getline(infile, line)) {
        sscanf(line.c_str(),
          "%[^|]|%[^|]|%*[^|]|%[^|]|%*[^|]|%*[^|]|%*[^|]|%[^|]|%*[^|]",
          temp_order, temp_cust, temp_price, temp_priority);
        page.rec.orderkey[count] = strtod(temp_order, NULL);
        page.rec.custkey[count] = strtod(temp_cust, NULL);
        page.rec.totalprice[count] = strtod(temp_price, NULL);
        page.rec.shippriority[count] = strtod(temp_priority, NULL);
        count++;

        if (count == 1024) {
          fwrite(page.rec.orderkey, sizeof(double), 1024, order);
          fwrite(page.rec.custkey, sizeof(double), 1024, cust);
          fwrite(page.rec.totalprice, sizeof(double), 1024, price);
          fwrite(page.rec.shippriority, sizeof(double), 1024, priority);

          count = 0;
        }
      }

      fwrite(page.rec.orderkey, sizeof(double), count, order);
      fwrite(page.rec.custkey, sizeof(double), count, cust);
      fwrite(page.rec.totalprice, sizeof(double), count, price);
      fwrite(page.rec.shippriority, sizeof(double), count, priority);

      infile.close();
      fclose(order);
      fclose(cust);
      fclose(price);
      fclose(priority);

      flag = true;
      std::cout << "load succeed" << std::endl;
    } else {
      const char* filename = loadFileName(tableName);
      std::ifstream infile(filename);
      FILE *fp = fopen(builtFileName(tableName), "wb");
      char temp[20];
      double key[1024];

      while (getline(infile, line)) {
        sscanf(line.c_str(), "%[^|]", temp);
        key[count] = strtod(temp, NULL);
        count++;

        if (count == 1024) {
          fwrite(key, sizeof(double), 1024, fp);
          count = 0;
        }
      }

      fwrite(key, sizeof(double), count, fp);

      infile.close();
      fclose(fp);

      flag = true;
      std::cout << "load succeed" << std::endl;
    }

  time_t end = clock();
  printf("total time: %f seconds\n", (end - start) * 1.0 / CLOCKS_PER_SEC);

  return flag;
}

void Service::retrieve(std::string tableName, double primarykey) {
  time_t start = clock();
  buf = new BufferManager();
  Page *page = buf->getPage();
  double* result = NULL;
  bool flag = false;
  int i;

  if (tableName == "orders") {
      int dataPage = buf->readData();

      result = new double[4];
      while (page[buf->getPagenum()-1].rec.orderkey[dataPage-1] < primarykey) {
        dataPage = buf->readData();

        // the files have been read completely
        if (buf->getPagenum() == 0) {
          std::cout << "No search result" << std::endl;
          return;
        }
      }

      int pageID = 0;
      while (pageID < buf->getPagenum()-1 &&
        page[pageID].rec.orderkey[1023] < primarykey)
        pageID++;

      int front = 0, end;
      if (pageID == buf->getPagenum()-1)
        end = dataPage-1;
      else
        end = 1023;
      int mid = (front+end) / 2;

      while (front < end && page[pageID].rec.orderkey[mid] != primarykey) {
        if (page[pageID].rec.orderkey[mid] < primarykey)
          front = mid + 1;
        if (page[pageID].rec.orderkey[mid] > primarykey)
          end = mid - 1;
        mid = (front+end) / 2;
      }

      if (page[pageID].rec.orderkey[mid] != primarykey) {
        std::cout << "No search result" << std::endl;
      } else {
          result[0] = page[pageID].rec.orderkey[mid];
          result[1] = page[pageID].rec.custkey[mid];
          result[2] = page[pageID].rec.totalprice[mid];
          result[3] = page[pageID].rec.shippriority[mid];

          std::cout << std::setiosflags(std::ios::left) << std::setw(15)
            << "orderkey" << std::setw(15) << "custkey" << std::setw(15)
            << "totalprice" << std::setw(15) << "shippriority" << std::endl;
          std::cout << std::setiosflags(std::ios::left) << std::setw(15)
            << std::fixed << std::setprecision(4) << result[0] << std::setw(15)
            << result[1] << std::setw(15) << result[2] << std::setw(15)
            << result[3] << std::endl;

          time_t end = clock();
          printf("retrieve time: %f seconds\n",
            (end - start) * 1.0 / CLOCKS_PER_SEC);
        }
  } else {
      std::cout << "Can not find the targe file, please load first"
        << std::endl;
      return;
    }
}

float Service::compress(std::string tableName, std::string column) {
  time_t start = clock();

  char* compressedDir = new char[100];
  char* fileName = new char[50];

  if (tableName == "orders" && column == "1") {
    sprintf(compressedDir, "compressed_custkey");
  } else {
      std::cout << "Please input the fileName after compressing: ";
      std::cin >> fileName;
      sprintf(compressedDir, "%s", fileName);
    }

  std::cout << "Compressing..." << std::endl;

  char* colName = columnName(column);
  if (!colName)
    return -1;

  // External Sort
  ExternalSort extSort(colName, colName, NUMBER_TO_SORT);
  char* sorted_file = extSort.sort();

  // compress
  struct VL {
    double value;
    double length;
    VL(double v, double l) {
      value = v;
      length = l;
    }
  };

  std::ifstream fin;
  std::ofstream fout;
  fin.open(sorted_file, std::ios::in|std::ios::binary);
  fout.open(compressedDir, std::ios::out|std::ios::binary);
  while (!fin.eof()) {
    double value, length, temp;
    fin.read((char*)&value, sizeof(double));
    length = 1;
    while (fin.read((char*)&temp, sizeof(double))) {
      if (temp == value) {
        length++;
      } else {
          fin.seekg(-(long)(sizeof(double)), std::ios::cur);
          break;
        }
    }
    VL vl(value, length);
    fout.write((char*)&vl, sizeof(VL));
  }

  double beforeCompress = getFileSize(sorted_file);
  double afterCompress = getFileSize(compressedDir);
  double efficiency = afterCompress/beforeCompress*100;

  delete colName;
  delete fileName;

  fin.close();
  fout.close();

  std::cout << "compress succeed" << std::endl;

  time_t end = clock();
  printf("total time: %f seconds\n", (end - start) * 1.0 / CLOCKS_PER_SEC);

  return efficiency;
}

void Service::join() {
  time_t start = clock();

  FILE* customerkey = fopen("customer.bin", "rb");
  FILE* compressed_custkey = fopen("compressed_custkey", "rb");
  FILE* e_orderkey = fopen("Esorted_orderkey.bin", "rb");

  if (customerkey == NULL) {
    std::cout << "Can not find the file of custkey of customer, please 'load customer' first\n"
      << std::endl;
    return;
  } else if (compressed_custkey == NULL) {
        std::cout << "Can not find the compressed file, please 'compress orders 2' first\n"
          << std::endl;
        return;
    } else if (e_orderkey == NULL) {
          std::cout << "Can not find the Esorted_orderkey.bin, please 'compress orders 2' first\n"
            << std::endl;
          return;
        }

  double getCustomerkey;
  double getCompressedkey[10];
  double getOrderkey[100];
  int counter = 0;
  int rowNumber = 0;

  std::cout << std::setiosflags(std::ios::left) << std::setw(15) << "custkey"
    << std::setw(15) << "orderkey" << std::endl;

  while (int controller1 = fread(&getCustomerkey, sizeof(double), 1, customerkey)) {
      fseek(compressed_custkey, counter*2*sizeof(double), SEEK_SET);

      while (int controller2 = fread(getCompressedkey, sizeof(double), 2, compressed_custkey)) {
        if (getCustomerkey == getCompressedkey[0]) {
          int length = (int)getCompressedkey[1];
          fread(getOrderkey, sizeof(double), length, e_orderkey);

          for (int i = 0; i < length; i++)
            std::cout << std::setiosflags(std::ios::left) << std::setw(15) << std::fixed
              << std::setprecision(4) << getCustomerkey << std::setw(15)
              << getOrderkey[i] << std::endl;

          counter++;
          rowNumber += length;

          break;
        } else if (getCustomerkey < getCompressedkey[0])
              break;
           else if (getCustomerkey > getCompressedkey[0])
                counter++;
      }
  }
  std::cout << "totlal number: " << rowNumber << " rows" << std::endl;

  fclose(customerkey);
  fclose(compressed_custkey);
  fclose(e_orderkey);

  time_t end = clock();
  printf("total time: %f seconds\n", (end - start) * 1.0 / CLOCKS_PER_SEC);
}

int Service::count(std::string tableName) {
  FILE* fp = fopen("compressed_custkey", "rb");
  if (fp == NULL)
    return 0;

  int totalNumber = 0;
  double value[2];
  while (fread(value, sizeof(double), 2, fp))
    totalNumber += value[1];

  return totalNumber;
}

double Service::getFileSize(char *fileName) {
  FILE *fp = fopen(fileName, "rb");
  fseek(fp, 0L, SEEK_END);
  double size = ftell(fp);
  fclose(fp);
  return size;
}

char* Service::columnName(std::string col) {
  char *colName = new char[20];

  if (col == "0") {
      if (!fopen("orderkey.bin", "r"))
        return NULL;
      else
        sprintf(colName, "orderkey");
  } else if (col == "1") {
        if (!fopen("custkey.bin", "r"))
          return NULL;
        else
          sprintf(colName, "custkey");
      } else if (col == "2") {
          if (!fopen("totalprice.bin", "r"))
            return NULL;
          else
            sprintf(colName, "totalprice");
        } else if (col == "3") {
            if (!fopen("shippriority.bin", "r"))
              return NULL;
            else
              sprintf(colName, "shippriority");
          }

  return colName;
}

const char* Service::loadFileName(std::string tableName) {
  std::string filename = "../" + tableName + ".tbl";
  return filename.c_str();
}

const char* Service::builtFileName(std::string property) {
  std::string filename = property + ".bin";
  return filename.c_str();
}
