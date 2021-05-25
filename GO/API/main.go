package main

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"sort"
)

type HistoryArray struct {
	Task    string      `json:"task"`
	Numbers []int       `json:"numbers"`
	Answer  interface{} `json:"answer"`
}

type History struct {
	Size    int           `json:"size"`
	History []interface{} `json:"history"`
	Code    int           `json:"code"`
	Message string        `json:"message"`
}

type Request struct {
	Task string `json:"task"`
	Numbers []int `json:"numbers"`
}

type MeanReponse struct {
	Task    string `json:"Task"`
	Code    int    `json:"code"`
	Message string `json:"message"`
	Numbers []int `json:"numbers"`
	Answer float64 `json:"answer"`
}
type SortReponse struct {
	Task    string `json:"Task"`
	Code    int    `json:"code"`
	Message string `json:"message"`
	Numbers []int `json:"numbers"`
	Answer []int `json:"answer"`
}

type error struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}

var Error error
var mean MeanReponse
var Sort SortReponse
var historyarray []HistoryArray

var history History = History{
	Size: 0,
}

func calculatorHandler(w http.ResponseWriter, req *http.Request) {
	if req.Method != "POST" {
		w.WriteHeader(405)
		//fmt.Fprintf(w, "405 error : method not allowed\n")
		Error.Code = 405
		Error.Message = "method not allowed"

		jsonBytes, _ := json.Marshal(Error)
		w.Write(jsonBytes)
		return
	}

	var request Request
	var unmarshalErr *json.UnmarshalTypeError
	//err := json.NewDecoder(req.Body).Decode(&request)
	decode := json.NewDecoder(req.Body)
	decode.DisallowUnknownFields()
	err := decode.Decode(&request)
	if err != nil {
		if errors.As(err, &unmarshalErr) {
			w.WriteHeader(400)
			errorhandler(http.StatusBadRequest, "wrong type for field "+unmarshalErr.Field)
			jsonBytes, _ := json.Marshal(Error)
			w.Write(jsonBytes)
		}else {
			w.WriteHeader(400)
			//fmt.Fprintf(w, "400 error : bad request\n")
			errorhandler(http.StatusBadRequest, "bad request "+ err.Error())
			jsonBytes, _ := json.Marshal(Error)
			w.Write(jsonBytes)
		}
		return
	}

	var result float64 = 0
	//var sorted []int
	if request.Task == "mean"{
		for i := 0; i < len(request.Numbers); i++ {
			result += float64(request.Numbers[i])
		}
		result /= float64(len(request.Numbers))

		history.Size++
		mean = MeanReponse{
			Task:    request.Task,
			Code:    200,
			Message: "Task done successfully!",
			Answer:  result,
			Numbers: request.Numbers,
		}
		jsonBytes, _ := json.Marshal(mean)

		w.Write(jsonBytes)

		hist := HistoryArray{request.Task, request.Numbers, result}
		historyarray = append(historyarray, hist)

	} else if request.Task == "sort"{

		sorted := append([]int{}, request.Numbers...)
		sort.Ints(sorted)

		history.Size++
		Sort = SortReponse{
			Task:    request.Task,
			Code:    200,
			Message: "Task done successfully! sort",
			Answer:  sorted,
			Numbers: request.Numbers,
		}
		jsonBytes, _ := json.Marshal(Sort)

		w.Write(jsonBytes)

		hist := HistoryArray{request.Task, request.Numbers, sorted}
		historyarray = append(historyarray, hist)

	} else{
		errorhandler(http.StatusBadRequest, "Task is not valid")
		jsonBytes, _ := json.Marshal(Error)
		w.Write(jsonBytes)
	}
}

func historyHandler(w http.ResponseWriter, req *http.Request) {
	if req.Method != "GET" {
		w.WriteHeader(405)
		//fmt.Fprintf(w, "405 error : method not allowed\n")
		errorhandler(http.StatusMethodNotAllowed,"method not allowed")
		errorhandler(404, "page not found")
		jsonBytes, _ := json.Marshal(Error)
		w.Write(jsonBytes)
		return
	}
	w.WriteHeader(200)
	data := make([]interface{}, len(historyarray))
	for i := 0; i < len(historyarray); i++ {
		data[i] = historyarray[i]
	}
	result := History{
		len(data),
		data,
		200,
		"History sent successfully!",
	}
	response, _ := json.Marshal(result)
	w.Write(response)
}

func notFoundHandler(w http.ResponseWriter, req *http.Request) {
	w.WriteHeader(404)
	//fmt.Fprintf(w, "404 error : not found\n")
	errorhandler(404, "page not found")
	jsonBytes, err := json.Marshal(Error)
	if err != nil {
		// TODO : handle error
	}
	w.Write(jsonBytes)
}

func errorhandler(errorcode int, message string)  {
	Error.Code = errorcode
	Error.Message = message
}

func main() {

	http.HandleFunc("/calculator", calculatorHandler)
	http.HandleFunc("/history", historyHandler)
	http.HandleFunc("/", notFoundHandler)

	log.Fatal(http.ListenAndServe(":80", nil))
}