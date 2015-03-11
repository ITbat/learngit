#ifndef PARSER_H
#define PARSER_H

#include <vector>
#include "Lexer.h"

class Parser {
public:
	Parser(FILE *fp);
	~Parser();
	void analyse();

	void ssql_stmt();
	void create_stmt();
	void insert_stmt();
	void query_stmt();
	void delete_stmt();

	void decl_list();
    void decl();
    void default_spec();
    void disjunct();
    void conjunct();
    void column_list();
    void comp();
    void expr();
    void value_list();
    void where_clause();
    void conjunct_list();
    void _bool();
    void operand();
    void rop();
    void term();
    void unary();
    void select_list();
    void simple_expr();
    void simple_term();
    void simple_unary();
	void match(std::string str);

	void SyntaxError();
	void toNextStatement();
	Token* getLookahead();
	vector<Token*> getTokenStream();
	Stmt getStmt();
private:
	std::vector<Token*> token_stream;
	Lexer *lex;
	Token *lookahead;
	Stmt stmt;
	bool isError;
};

#endif