Ssql: Main.o Lexer.o Parser.o Translator.o Error.o Calculator.o DataBase.o Table.o
	g++ -o bin/Ssql bin/Main.o bin/Lexer.o bin/Parser.o bin/Translator.o bin/Error.o bin/Calculator.o bin/DataBase.o bin/Table.o
Lexer.o: src/Lexer.cpp src/Data.h src/Lexer.h
	g++ -c src/Lexer.cpp -o bin/Lexer.o
Main.o: src/Main.cpp src/Translator.cpp
	g++ -c src/Main.cpp -o bin/Main.o
Parser.o: src/Parser.cpp src/Parser.h
	g++ -c src/Parser.cpp -o bin/Parser.o
Translator.o: src/Translator.cpp src/Translator.h src/Error.h
	g++ -c src/Translator.cpp -o bin/Translator.o
Error.o: src/Error.cpp src/Error.h
	g++ -c src/Error.cpp -o bin/Error.o
Calculator.o: src/Calculator.cpp src/Calculator.h src/Data.h
	g++ -c src/Calculator.cpp -o bin/Calculator.o
DataBase.o: src/DataBase.cpp src/DataBase.h src/Table.h src/Data.h
	g++ -c src/DataBase.cpp -o bin/DataBase.o
Table.o: src/Table.cpp src/Table.h src/Calculator.h src/Data.h
	g++ -c src/Table.cpp -o bin/Table.o

clean:
	rm -rf bin/Ssql bin/Main.o bin/Lexer.o bin/Parser.o bin/Translator.o bin/Error.o bin/Calculator.o bin/Table.o bin/DataBase.o
