package main

import (
    "fmt"
    "net"
	"math"
	"math/rand"
	"strings"
	"encoding/binary"
	"time"
	"encoding/hex"
)

//start a tcp server
func tcp() {
	//update your ip
	listener, err := net.Listen("tcp", "192.168.31.59:2600")
    if err != nil {
		fmt.Println("Error listening", err.Error())
		return
	}

	defer listener.Close()
	fmt.Println("Listening on localhost:2600...")

	//accept tcp client
	for {
		conn, err := listener.Accept()
		if err!= nil {
			fmt.Println("Error accepting:", err.Error())
            continue
		}

		go handleRequest(conn)
		
	}

}

func handleRequest(conn net.Conn) {
	defer conn.Close()

	//handle receive
	go func() {
		for {
			buffer := make([]byte, 512)
			n, err := conn.Read(buffer)

			if err != nil {
				fmt.Println("Error reading:", err.Error())
				return
			}
			fmt.Println("Received data:", string(buffer[:n]))
		}
    }()

	
	//transmit a hexstring when a tcp client connectiong
	for {
		hexString := combination()
		
		bytes, err := hex.DecodeString(hexString)
		if err != nil {
			fmt.Println("Error decoding hex string:", err)
			return
		}

		_, err = conn.Write(bytes)
		if err != nil {
			fmt.Println("Error writing to connection:", err)
			return
		}

		fmt.Println("Message sent:", hexString)

		time.Sleep(500 * time.Millisecond) 
	}
}

func combination() string {
    y, y2 ,y3 := mockRandomFloat()
	value := "AAFF55" + "0C" + y + y2 + y3 + "BBBB"
	value = strings.ReplaceAll(value, " ", "")
	fmt.Println("combination:", value)
	return value
}

func floatConvertHex(value float32) (string) {
	buffer := make([]byte, 4)
	binary.LittleEndian.PutUint32(buffer, math.Float32bits(value))

	hexString := ""
	for _, b := range buffer {
		hexString += fmt.Sprintf("%02X ", b)
	}
	hexString = hexString[:len(hexString)-1]

	fmt.Printf("hexString:%s\n", hexString)
	return hexString
}

func mockRandomFloat() (string, string, string) {
	nextY := rand.Float32() * 15
    y := floatConvertHex(nextY)
	y2 := floatConvertHex(nextY * rand.Float32())
	y3 := floatConvertHex(nextY * rand.Float32())
	return y, y2, y3
}
 
func main() {
	tcp()
}