package main

import (
	"bufio"
	"fmt"
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

	sum := 0
	runes := []rune(inp)

	for i := 0; i < len(runes); i++ {
		sum += int(runes[i])
	}
	if sum<200 {
		fmt.Println("err")
	}else {
		for sum > 400 {
			sum /= 2
		}
		fmt.Println(sum)
	}

}