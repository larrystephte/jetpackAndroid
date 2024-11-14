package main

import (
	"fmt"
	"log"
	"github.com/tarm/serial"
	"time"
	"math"
	"math/rand"
	"encoding/binary"
	"encoding/hex"
	"strings"
)

func serail() {
	config := &serial.Config{
		Name:        "COM3",             // Serial port name
		Baud:        9600,               // Baud rate
		// Parity:      serial.NoParity,    // NoParity
		Size:        8,                  // Data bits(8)
		// StopBits:    serial.OneStopBit,  // 1 stop bit
		ReadTimeout: time.Second * 2,    // Read timeout
	}

	// Open the serial port
	port, err := serial.OpenPort(config)
	if err != nil {
		log.Fatal(err)
	}
	defer port.Close()

	// start receive and send goroutines
	go receiveData(port)
	go sendData(port)

	select {}
}

func receiveData(port *serial.Port) {
	buffer := make([]byte, 128) // Set the receive buffer size
	message := ""               // store the concatenated data

	for {
		// Read data from the serial port
		n, err := port.Read(buffer)
		if err != nil {
			fmt.Println("Failed to read serial data:", err)
			return
		}
		if n > 0 {
			// Append the received data to the message
			message += string(buffer[:n])

			// If the received data contains a newline character, consider it a complete message
			if message[len(message)-1] == '\n' {
				// Print the complete message
				fmt.Printf("Received complete message: %s", message)
				//reset message to receive the next message
				message = ""
			}
		}
	}
}

// Goroutine to send data to the serial port
func sendData(port *serial.Port) {
	for {
		// construct the data to be sent
		// message := "Hello, Serial Port!\n"
		hexString := combination()
		
		bytes, err := hex.DecodeString(hexString)
		if err != nil {
			fmt.Println("Error decoding hex string:", err)
			return
		}

		// Write the data to the serial port
		_, err = port.Write(bytes)
		// _, err := port.Write([]byte(message))
		if err != nil {
			fmt.Println("Failed to send data:", err)
			return
		}

		// Print the sent message
		fmt.Printf("Sent data: %s\n", hexString)

		// Wait for 2 seconds before sending data again
		time.Sleep(2 * time.Second)
	}
}


func combination() string {
    y, y2 ,y3 := mockRandomFloat()
	value := "AAFF55" + "0C" + y + y2 + y3 + "BBBB"
	value = strings.ReplaceAll(value, " ", "")
	// fmt.Println("combination:", value)
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

	// fmt.Printf("hexString:%s\n", hexString)
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
	serail()
}