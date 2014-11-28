GEN_SRC := gen-cpp/SharedService.cpp gen-cpp/shared_types.cpp gen-cpp/tutorial_types.cpp gen-cpp/Calculator.cpp
GEN_OBJ := $(patsubst %.cpp,%.o, $(GEN_SRC))

THRIFT_DIR := /usr/local/include/thrift
BOOST_DIR := /usr/local/include
GEN_INC := gen-cpp
LIBS := -lssl -lthrift

INC := -I$(THRIFT_DIR) -I$(BOOST_DIR) -I$(GEN_INC)

.PHONY: all clean

thrift:
	thrift -r --gen java commit_server.thrift ;

clean:
	$(RM) *.o server client
