package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main()  {


	scanner := bufio.NewScanner(os.Stdin)

	var name string
	var m int
	var feature []string
	var check []string
	b := false

	fmt.Scanf("%s %d", &name, &m)

	for  i := 0; i < m; i++{
		scanner.Scan()
		f := strings.TrimSpace(scanner.Text())
		feature = append(feature, f)
		//println(feature[i])
	}
	scanner.Scan()
	n, _ := strconv.Atoi(scanner.Text())
	//println(n)
	for i := 0; i<n; i++{
		scanner.Scan()
		f := strings.TrimSpace(scanner.Text())
		check = append(check, f)
		}
		//println(n, m)
	for i := 0; i < n; i++ {
		for j := 0; j < m; j++{
			//println("e")
			if strings.ToLower(check[i]) == strings.ToLower(feature[j]) {
				fmt.Println("yes")
				b = true
				break
			}
		}
		if b == false {
			fmt.Println("no")
		}
		b = false
		}

}
