MyDatabase: Main.o UI.o BufferManager.o Service.o ExternalSort.o Function.o
	g++ -o bin/MyDatabase bin/Main.o bin/UI.o bin/BufferManager.o bin/Service.o bin/ExternalSort.o bin/Function.o
Main.o: src/Main.cpp src/UI.h src/DataType.h
	g++ -c src/Main.cpp -o bin/Main.o
UI.o: src/UI.cpp src/UI.h src/DataType.h
	g++ -c ./src/UI.cpp -o bin/UI.o
BufferManager.o: src/BufferManager.cpp src/BufferManager.h src/DataType.h
	g++ -c src/BufferManager.cpp -o bin/BufferManager.o
Service.o: src/Service.cpp src/Service.h src/DataType.h src/ExternalSort.h
	g++ -c ./src/Service.cpp -o bin/Service.o
ExternalSort.o: src/ExternalSort.cpp src/ExternalSort.h
	g++ -c src/ExternalSort.cpp -o bin/ExternalSort.o
Function.o: src/Function.cpp src/Function.h
	g++ -c src/Function.cpp -o bin/Function.o

clean:
	rm -rf bin/MyDatabase bin/Main.o bin/UI.o bin/BufferManager.o bin/Service.o bin/ExternalSort.o bin/Function.o