GEN_SRC := gen-cpp/SharedService.cpp gen-cpp/shared_types.cpp gen-cpp/tutorial_types.cpp gen-cpp/Calculator.cpp
GEN_OBJ := $(patsubst %.cpp,%.o, $(GEN_SRC))

THRIFT_DIR := /usr/local/include/thrift
BOOST_DIR := /usr/local/include
GEN_INC := gen-cpp
LIBS := -lssl -lthrift

INC := -I$(THRIFT_DIR) -I$(BOOST_DIR) -I$(GEN_INC)

.PHONY: all clean

all: Calculator_server Calculator_client

%.o: %.cpp
	$(CXX) -Wall -DHAVE_INTTYPES_H -DHAVE_NETINET_IN_H $(INC) -c $< -o $@

Calculator_server: Calculator_server.o $(GEN_OBJ)
	$(CXX) $^ -o $@ -L/usr/local/lib $(LIBS)

Calculator_client: Calculator_client.o $(GEN_OBJ)
	$(CXX) $^ -o $@ -L/usr/local/lib $(LIBS)

thrift:
	thrift -r --gen cpp shared.thrift ;
	thrift -r --gen cpp tutorial.thrift

clean:
	$(RM) *.o server client
