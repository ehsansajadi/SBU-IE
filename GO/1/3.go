package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"os"
	"strconv"
)

type course struct {
	Name    string		 `json:"name"`
	ID      int
	Teacher string
	Unit    int
}

func (c course) String() string {
	return fmt.Sprintf("%v (%v)\nby %v", c.Name, c.Unit, c.Teacher)
}

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

	var c course
	json.Unmarshal(data, &c)
	fmt.Println(c)

		//fmt.Printf("%v (%v)\n", courses.Name, courses.Unit)
		//fmt.Println("by " + courses.Teacher)
		//fmt.Println(courses.TAs)
}