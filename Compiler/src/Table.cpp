#ifndef TABLE_CPP
#define TABLE_CPP

#include "Table.h"

Table::Table() { cal = new Calculator(); }
        
Table::Table(string name, map<string,int> cols,vector<string> pk) {
	cal = new Calculator();
    table_name = name;
    col_name = cols;  
    map<string,int>::iterator it;
    vector<string>::iterator pk_it;
    for (it = col_name.begin(); it != col_name.end(); it++) {
        int flag = 0;
        for (pk_it = pk.begin(); pk_it != pk.end(); pk_it++) {
        	if((it->first) == (*pk_it)) {
            	flag = 1;
             	break;
      		}    
  		}  
        if(flag == 0) {
            prikeys.push_back(false);
        }      
        else {
            prikeys.push_back(true);
        }    
    }    
}

map<string,int> Table::reorder(map<string,int> col) {
	map<string,int> temp_col;
	map<string,int>::iterator col_it;
	map<string,int>::iterator it;
	for (it = col_name.begin(); it != col_name.end(); it++) {
		int flag = 0;
		for (col_it = col.begin(); col_it != col.end(); col_it++) {
			if(it->first == col_it->first) {
 				flag = 1;
 				temp_col.insert(map<string,int>::value_type(col_it->first,col_it->second));
				break;
			}  
		}    
		if(flag == 0) {
			temp_col.insert(map<string,int>::value_type(it->first,it->second));
		}    
    }  
    return temp_col;  
}    
        
bool Table::check(map<string,int> col) {
	if(col.size() > length) {
		return false;
	}    
	map<string,int>::iterator col_it;
	map<string,int>::iterator it;
	for (col_it = col.begin(); col_it != col.end(); col_it++) {
        int flag = 0;
        for (it = col_name.begin(); it != col_name.end(); it++) {
            if(it->first == col_it->first) {
                flag = 1;
                break;
            }  
        }
        if(flag == 0) {
            return false;
        }        
    }  
    return true;  
}    
        
bool Table::is_exist(map<string,int> col) {
	vector< vector<int> >::iterator it;
	map<string,int>::iterator col_it;
	vector<int>::iterator tcol_it;
 	vector<bool>::iterator pk_it;
	int t = 0;
	for(pk_it = prikeys.begin(); pk_it != prikeys.end(); pk_it++) {
		if((*pk_it) == true) {
			t = 1;
  			break;
    	}    
	}    
    if(t == 0) {
        return false;
    }    
 	for(it = col_list.begin(); it != col_list.end(); it++) {
		pk_it = prikeys.begin();
        tcol_it = (*it).begin();
        int flag = 0;
        for(col_it = col.begin(); col_it != col.end(); col_it++) {
            if((*pk_it) == true) {
                if((col_it->second) != (*tcol_it)) {
                    flag = 1;
                    break;
                }    
            }    
            pk_it++;
            tcol_it++;
        } 
        if(flag == 0) {
            return true;
        } 
    } 
    return false;   
}    
        
void Table::insert(map<string,int> col) {
	if(check(col) == false) {
		cout << "some colunms are not in this table" <<endl; 
	} else {
		map<string,int> temp_col = reorder(col);
        if(is_exist(temp_col) == true) {
            cout << "this record has exist!" <<endl;
        } else {
            vector<int> rows;
            map<string,int>::iterator it;
            for(it = temp_col.begin(); it != temp_col.end(); it++) {
                rows.push_back(it->second);
            }    
            col_list.push_back(rows);
            cout<< "insert successful!" <<endl;
        }      
    }    
                
}       
        
void Table::remove(map<string,int> col,vector<Token*> tokens) {
	if(check(col) == false) {
		cout << "some colunms are not in this table" <<endl; 
	} else {
		vector<string> temp_name;
		vector<int> temp_row;
		map<string,int>::iterator name_it;
		vector< vector<int> >::iterator row_it;
		bool flag = false;
		for(name_it = col_name.begin(); name_it != col_name.end(); name_it++) {
    		temp_name.push_back(name_it->first);
		}    
		for(row_it = col_list.begin(); row_it != col_list.end(); row_it++) {  
			if(cal->cal_condition(temp_name,*row_it,tokens) == true) {
				flag = true;
				/*if (row_it == col_list.end()-1) {
					col_list.erase(row_it);
					break;
				}*/
    	     	col_list.erase(row_it);
                row_it--;

    		}    
		}
		if (flag)
			cout << "delete successful!" << endl;
		else
			cout << "No entry satisfying the condition" << endl;
	}
}       
        
void Table::query(map<string,int> col,vector<Token*> tokens) {
	vector<string> temp_name;
	vector<int> temp_row;
	vector<int>::iterator it;
	vector<string>::iterator sit;
	map<string,int>::iterator name_it;
	vector< vector<int> >::iterator row_it;
    
    if(col_list.size() == 0) {
        cout << "there is no record in this table"<<endl;
        return ;
    }

    for(name_it = col_name.begin(); name_it != col_name.end(); name_it++) {
        temp_name.push_back(name_it->first);
    }   

    name_it = col.begin();
    if(name_it->first == "*") {
        int line_length = 1;
        for(name_it = col_name.begin(); name_it != col_name.end(); name_it++) {
            line_length+=len_line(name_it->first)+1;
        } 

        if(tokens.size() == 0) {
        	int n = 0;
        	print_line(line_length);
			for(name_it = col_name.begin(); name_it != col_name.end(); name_it++) {
            	name_print(name_it->first,len_line(name_it->first));
       		} 
        	cout<<"|"<<endl;
        	print_line(line_length);
            for(row_it = col_list.begin(); row_it != col_list.end(); row_it++) {
                name_it = col_name.begin();
                for(it = (*row_it).begin(); it != (*row_it).end(); it++) {
                    value_print((*it), len_line((name_it->first)));
                //cout << (*sit) << "\t"; 
                    name_it++;
                }  
                n++;
                cout<<"|"<<endl;
                print_line(line_length);
            }
            cout << "query successful! " << n << " rows affected!"<<endl;
        } else {
            bool flag = false;
            for(row_it = col_list.begin(); row_it != col_list.end(); row_it++) {
            	bool condition = cal->cal_condition(temp_name,*row_it,tokens);
            	if(cal->isZeroError()) {
            		cout<< "ZeroError!" <<endl;
            		cal->resetZeroError(false);
            		return;
            	}
                if(condition == true) {
                    name_it = col_name.begin();
                    for(it = (*row_it).begin(); it != (*row_it).end(); it++) {
                        flag = true;
                        break;
                    }  
                }
            }
            if(flag) {
            	print_line(line_length);
				for(name_it = col_name.begin(); name_it != col_name.end(); name_it++) {
            		name_print(name_it->first,len_line(name_it->first));
       			} 
        		cout<<"|"<<endl;
        		print_line(line_length);
            }
            flag = false;
            int n = 0;
            for(row_it = col_list.begin(); row_it != col_list.end(); row_it++) {
                if(cal->cal_condition(temp_name,*row_it,tokens) == true) {
                    name_it = col_name.begin();
                    for(it = (*row_it).begin(); it != (*row_it).end(); it++) {
                        flag = true;
                        value_print((*it), len_line((name_it->first)));
                //cout << (*sit) << "\t";  
                        name_it++;
                    }  
                    n++;
                    cout<<"|"<<endl;
                    print_line(line_length);
                }
            }
            if (flag)
                cout << "query successful! " << n <<" rows affected!"<<endl;
            else
                cout << "No entry satisfying the condition" << endl; 
        }

        return;
        
    }

    if(check(col) == true) { 

        int line_length = 1;
        for(name_it = col.begin(); name_it != col.end(); name_it++) {
            line_length+=len_line(name_it->first)+1;
        } 
        print_line(line_length);

	    for(name_it = col.begin(); name_it != col.end(); name_it++) {
            name_print(name_it->first,len_line(name_it->first));
	    } 
        cout<<"|"<<endl;
	    print_line(line_length);

	    if(tokens.size() == 0) {
	    	int n = 0;
	      	for(row_it = col_list.begin(); row_it != col_list.end(); row_it++) {
        		for(name_it = col.begin(); name_it != col.end(); name_it ++) {
           			sit = temp_name.begin();
        			for(it = (*row_it).begin(); it != (*row_it).end(); it++) {
                		if( (*sit) == (name_it->first)) {
                            value_print((*it), len_line(name_it->first));
                    		//cout << (*it) << "\t";
                    		break;
                    	}    
                    	sit++;
               		}  
            	} 
            	n++;
	       	    cout<<"|"<<endl;
                print_line(line_length);
            }
            cout << "query successful! " << n <<" rows affected!"<<endl;

        } else {   
            bool flag = false;
            int n = 0;
		    for(row_it = col_list.begin(); row_it != col_list.end(); row_it++) {
		    	bool condition = cal->cal_condition(temp_name,*row_it,tokens);
            	if(cal->isZeroError()) {
            		cout<< "ZeroError!" <<endl;
            		cal->resetZeroError(false);
            		return;
            	}
		        if(condition == true) {
				    for(name_it = col.begin(); name_it != col.end(); name_it ++) {
       				    sit = temp_name.begin();
    				    for(it = (*row_it).begin(); it != (*row_it).end(); it++) {
            			    if( (*sit) == (name_it->first)) {
                                flag = true;
                			    value_print((*it), len_line(name_it->first));
                			    break;
                		    }    
                		    sit++;
           			    }  
        	        } 
        	        n++;
                    cout<<"|"<<endl;
                    print_line(line_length);    
        	    }   
    	    } 
            if (flag)
                cout << "query successful! " << n <<" rows affected!"<<endl;
            else
                cout << "No entry satisfying the condition" << endl; 
        }
    } else {
        cout << "some colunms are not in this table" <<endl;
    }
}

void Table::print_line(int n) {
    for(int i = 0; i < n; i++) {
        cout<<"-";
    }    
    cout <<endl;
}

int Table::len_line(string s) {
    int length = 10;
    while(s.size() > length ) {
        length = length+5;
    }   
    return length;
}       

void Table::name_print(string s,int length) {
    int t = length - s.size();
    cout<<"|";
    for(int i = 0; i < t/2;i++ )
        cout<<" ";
    cout << s ;
    if(t%2 == 0) {
        for(int i = 0; i<t/2 ; i++ ) {
            cout<<" ";
        }    
    }   
    else {
        for(int i = 0; i<t/2 +1; i++ ) {
            cout<<" ";
        }  
    }    
}    

void Table::value_print(int s,int length) {   
    int l = 0;
    if(s<0) {
        l = 1;
    }
    int temp = s;
    if(temp == 0) {
        l = 1;
    } 
    
    else {   
        while( temp != 0) {
            temp = temp/10;
            l++;
        }
    }    
    
    int t = length - l;
    cout<<"|";
    for(int i = 0; i < t/2;i++ )
        cout<<" ";
    cout << s ;
    if(t%2 == 0) {
        for(int i = 0; i<t/2 ; i++ ) {
            cout<<" ";
        }    
    }   
    else {
        for(int i = 0; i<t/2 +1; i++ ) {
            cout<<" ";
        }  
    }    
}  
        
string Table::getName() { return table_name; } 
        
int Table::getLength() { return length; } 

#endif
