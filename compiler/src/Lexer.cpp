#ifndef LEXER_CPP
#define LEXER_CPP

#include <fstream>
#include "Error.h"
#include "Lexer.h"

Lexer::Lexer(FILE *_fp) {
	this->fp = _fp;
	line = 1;
	tokPtr = NULL;
}

Lexer::~Lexer(){}

int Lexer::isKey(std::string s) {
	for (int i = 0; i < KEY_NUM; i++) {
		if (Keys[i].compare(s) == 0)
			return 1;
	}
	return 0;
}

int Lexer::isLetter(char c) {
	return (((c <= 'z') && (c >= 'a')) || ((c <= 'Z')
				&& (c >= 'A'))|| (c == '_'));
}

int Lexer::isDigit(char c) {
	return (c >= '0' && c <= '9');
}

FILE* Lexer::getFP() { return fp; }

int Lexer::getLine() { return line; }

Token* Lexer::getNextToken() {
	analyse();
	while (tokPtr == NULL) {
		analyse();
	}
	return tokPtr;
}

void Lexer::analyse() {
	char ch;
	if ((ch = fgetc(fp)) != EOF) {
		std::string str = "";
		if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
			if (ch == '\n')
				line++;
			tokPtr = NULL;
		} else if (isLetter(ch)) {
			int counter = 1;
			while (isLetter(ch) || isDigit(ch)) {
				str += ch;
				ch = fgetc(fp);
				counter++;
			}
			if (!feof(fp))
				fseek(fp, -1L, SEEK_CUR);
			if (isKey(str))
				tokPtr = new Token(str, KEYWORD);
			else if (counter <= 64)
				tokPtr = new Token(str, ID);
			else
				tokPtr = NULL;
		} else if (isDigit(ch)) {
			bool flag = true;
			while (isDigit(ch) || isLetter(ch)) {
				if (isLetter(ch))
					flag = false;
				str += ch;
				ch = fgetc(fp);
			}
			if (!feof(fp))
				fseek(fp, -1L, SEEK_CUR);
			if (flag)
				tokPtr = new Token(str, NUM);
			else
				tokPtr = NULL;
		} else switch (ch) {
			case '+' : tokPtr = new Token("+", AOPERATOR); break;
			case '*' : tokPtr = new Token("*", AOPERATOR); break;
			case '/' : tokPtr = new Token("/", AOPERATOR); break;
			case '(' : tokPtr = new Token("(", OPERATOR); break;
			case ')' : tokPtr = new Token(")", OPERATOR); break;
			case '[' : tokPtr = new Token("[", OPERATOR); break;
			case ']' : tokPtr = new Token("]", OPERATOR); break;
			case ';' : tokPtr = new Token(";", END); break;
			case ',' : tokPtr = new Token(",", OPERATOR); break;
			case '!' : tokPtr = new Token("!", LOPERATOR); break;
			case '-' : tokPtr = new Token("-", AOPERATOR); break;
			case '=' : 
			{
				ch = fgetc(fp);
				if (ch == '=')
					tokPtr = new Token("==", LOPERATOR);
				else {
					if (!feof(fp))
						fseek(fp, -1L, SEEK_CUR);
					tokPtr = new Token("=", AOPERATOR);
				}
			} break;
			case '>' :
			{
				ch = fgetc(fp);
				if (ch == '=') 
					tokPtr = new Token(">=", LOPERATOR);
				else if (ch == '>')
					tokPtr = new Token(">>", AOPERATOR);
				else {
					if (!feof(fp))
						fseek(fp, -1L, SEEK_CUR);
					tokPtr = new Token(">", LOPERATOR);
				}
			} break;
			case '<' :
			{
				ch = fgetc(fp);
				if (ch == '=')
					tokPtr = new Token("<=", LOPERATOR);
				else if (ch == '<')
					tokPtr = new Token("<<", AOPERATOR);
				else if (ch == '>')
					tokPtr = new Token("<>", LOPERATOR);
				else {
					if (!feof(fp))
						fseek(fp, -1L, SEEK_CUR);
					tokPtr = new Token("<", LOPERATOR);
				}
			} break;
			case '&' :
			{
				ch = fgetc(fp);
				if (ch == '&')
					tokPtr = new Token("&&", LOPERATOR);
				else {
					if (!feof(fp))
						fseek(fp, -1L, SEEK_CUR);
					tokPtr = new Token("&", AOPERATOR);
				}
			} break;
			case '|' :
			{
				ch = fgetc(fp);
				if (ch == '|')
					tokPtr = new Token("||", LOPERATOR);
				else {
					if (!feof(fp))
						fseek(fp, -1L, SEEK_CUR);
					tokPtr = new Token("|", AOPERATOR);
				}
			} break;
			default : tokPtr = NULL;
		}
	} else {
		tokPtr = new Token("", _EOF);
	}
}

#endif