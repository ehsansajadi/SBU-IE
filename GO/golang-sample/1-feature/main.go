package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	var langName string
	var m int
	fmt.Scanf("%s %d", &langName, &m)

	scanner := bufio.NewScanner(os.Stdin)

	for i := 0; i < m; i++ {
		scanner.Scan()
		feature := strings.TrimSpace(scanner.Text())

		// process feature and append feature to a slice
	}

	scanner.Scan()
	n, _ := strconv.Atoi(scanner.Text())
	for i := 0; i < n; i++ {
		scanner.Scan()
		feature := strings.TrimSpace(scanner.Text())

		// process feature and check feature and print output

	}
}
