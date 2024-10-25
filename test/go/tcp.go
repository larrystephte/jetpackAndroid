package main

import (
    "fmt"
    "net"
	"math"
	"math/rand"
	"encoding/binary"
)

func tcp() {
	listener, err := net.Listen("tcp", "127.0.0.1:2600")
    if err != nil {
		fmt.Println("Error listening", err.Error())
		return
	}

	defer listener.Close()
	fmt.Println("Listening on localhost:2600...")

	for {
		conn, err := listener.Accept()
		if err!= nil {
			fmt.Println("Error accepting:", err.Error())
            continue
		}

		go handleRequest(conn)
	}

}

func to4ByteHex(num int) string {
	return fmt.Sprintf("%08x", num)
}


func handleRequest(conn net.Conn) {
	defer conn.Close()

	for {
		buffer := make([]byte, 512)
		n, err := conn.Read(buffer)

		if err != nil {
			fmt.Println("Error reading:", err.Error())
			return
		}
		fmt.Println("Received data:", string(buffer[:n]))
    }

	
	//_, err = conn.Write(buffer[:n])
    //if err != nil {
    //   fmt.Println("Error writing:", err.Error())
    //   return
    //}
}

func floatConvertHex(value float32) {
	//value := float32(-10)
	buffer := make([]byte, 4)
	binary.LittleEndian.PutUint32(buffer, math.Float32bits(value))

	hexString := ""
	for _, b := range buffer {
		hexString += fmt.Sprintf("%02X ", b)
	}
	hexString = hexString[:len(hexString)-1]

	fmt.Printf("hexString:%s\n", hexString)
}

func mockRandomFloat() {
	nextY := rand.Float32() * 15
    floatConvertHex(nextY)
	floatConvertHex(nextY * rand.Float32())
	floatConvertHex(nextY * rand.Float32())
}
 
func main() {
	
	mockRandomFloat()
	tcp()
}