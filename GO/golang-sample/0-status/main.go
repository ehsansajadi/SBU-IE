package main

import (
	"bufio"
	"os"
	"strings"
)

func main() {
	reader := bufio.NewReader(os.Stdin)
	inp, err := reader.ReadString('\n')
	if err != nil {
		panic("cannot read from stdin")
	}
	inp = strings.TrimSpace(inp)

	// use inp

}
