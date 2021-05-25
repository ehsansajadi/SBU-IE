package main

import (
	"bufio"
	"os"
	"strconv"
)

// define a struct

func main() {

	scanner := bufio.NewScanner(os.Stdin)

	scanner.Scan()
	n, err := strconv.Atoi(scanner.Text())
	if err != nil {
		panic(err)
	}
	data := make([]byte, 0)

	for i := 0; i < n; i++ {
		scanner.Scan()
		byteLine := []byte(scanner.Text())
		data = append(data, byteLine...) // append elements of slice to another
	}

	// unmarshal data and print
}
