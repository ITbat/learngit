#ifndef DATABASE_CPP
#define DATABASE_CPP

#include "DataBase.h"

DataBase::DataBase() {}
DataBase::~DataBase() {}

bool DataBase::table_exist(string name) {
	vector<Table>::iterator it;
	for(it = table_list.begin(); it != table_list.end(); it++) {
		if((*it).getName() == name) {
			return true;
        }    
	}    
	return false; 
}

void DataBase::create(string name, map<string,int> cols, vector<string> prikeys) {
	if (table_exist(name)) {
 		cout << "this table has exist!!"<<endl;
   	} else {
		Table table = Table(name,cols,prikeys);
    	table_list.push_back(table);
    	cout << "create successful!!"<<endl;
    }
}

void DataBase::insert(string name, map<string,int> cols) {
	vector<Table>::iterator it;
   	int flag = 0;
	for(it = table_list.begin(); it != table_list.end(); it++) {
       	if((*it).getName() == name) {
       		(*it).insert(cols);
            flag = 1;
         	break;
      	}    
  	} 
   	if(flag == 0) {
        cout << "this table don't exist!" << endl;
 	}
}

void DataBase::_delete(string name, map<string,int> col,vector<Token*> tokens) {
	if (table_exist(name)) {
   		if (tokens.empty()) {
          	delete_table(name);
       	} else {
          	vector<Table>::iterator it;
          	for(it = table_list.begin(); it != table_list.end(); it++) {
               	if((*it).getName() == name) {
                  	(*it).remove(col,tokens);
                   	break;
              	}    
           	}
       	}
   	} else {
       	cout << "this table don't exist!!"<<endl;
   	}
}

void DataBase::query(string name, map<string,int> cols, vector<Token*> tokens) {
	if (table_exist(name)) {
     	vector<Table>::iterator it;
    	for(it = table_list.begin(); it != table_list.end(); it++) {
         	if((*it).getName() == name) {
               	(*it).query(cols,tokens);
               	break;
           	}    
       	}  
   	} else {
      	cout << "this table don't exist!!"<<endl;
   	} 
}

void DataBase::delete_table(string name) {
	if (table_exist(name)) {
   		vector<Table>::iterator it;
       	for(it = table_list.begin(); it != table_list.end(); it++) {
           	if((*it).getName() == name) {
               	table_list.erase(it);
               	break;
            }    
       	}  
       	cout << "delete successful!!"<<endl;
  	} else {
      	cout << "this table don't exist!!"<<endl;
  	}
}

#endif