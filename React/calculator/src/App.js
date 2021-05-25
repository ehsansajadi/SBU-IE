import React from "react";

import Keypad from "./components/Keypad";
import Screen from "./components/Screen";
import "./App.css";

class App extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			firstNumber: 0,
			isNewNumber: true,
			operator: null,
			screenText: 0,
			isFloating: false,
			savedNumber: 0,
		};
	}

	handlePressAC = () => {
		this.setState({
			...this.state,
			isFloating: false,
			firstNumber: 0,
			screenText: 0,
			operator: null,
			isNewNumber: true,
		});
	};


	handlePressDigit = (digit) => {
		if (this.state.isNewNumber) {
			this.setState({
				...this.state,
				screenText: this.state.isFloating ? "0." + digit.toString() : digit,
				isNewNumber: false,
			});
		} else {
			this.setState({
				...this.state,
				screenText: this.state.isFloating
					? this.state.screenText + digit.toString()
					: this.state.screenText * 10 + digit,
				isNewNumber: false,
			});
		}
	};



	handlePressDot = () => {
		if (!this.state.isFloating)
			this.setState({
				...this.state,
				screenText: this.state.isNewNumber
					? "0."
					: this.state.screenText + ".",
				isFloating: true,
			});
	};




	handlePressOperator = (operator) => {
		this.setState({
			...this.state,
			firstNumber: this.state.screenText,
			screenText: 0,
			operator,
			isNewNumber: true,
			isFloating: false,
		});
	};


	handlePressResult = () => {
		let firstNumber = parseFloat(this.state.firstNumber);
		const screenText = parseFloat(this.state.screenText);
		switch (this.state.operator) {
			case "%":
				firstNumber %= screenText;
				break;
			case "/":
				firstNumber /= screenText;
				break;
			case "*":
				firstNumber *= screenText;
				break;
			case "-":
				firstNumber -= screenText;
				break;
			case "+":
				firstNumber += screenText;
				break;
			default:
		}
		this.setState({
			...this.state,
			screenText: firstNumber,
			firstNumber: 0,
			operator: null,
			isNewNumber: true,
			isFloating: false,
		});
	};


	handlePressNegator = () => {
		this.setState({
			...this.state,
			screenText: -1 * this.state.screenText,
			isNewNumber: false,
		});
	};


	handleMemoryPlus = () => {
		let savedNumber = parseFloat(this.state.savedNumber);
		const screenText = parseFloat(this.state.screenText);
		this.setState({
			...this.state,
			savedNumber: savedNumber + screenText,
			isNewNumber: true,
			isFloating: false,
		});
	};


	handleMemorySave = () => {
		this.setState({
			...this.state,
			savedNumber: this.state.screenText,
		});
	};


	handleMemoryRecall = () => {
		this.setState({
			...this.state,
			isNewNumber: true,
			isFloating: false,
			screenText: this.state.savedNumber,
		});
	};

	handleMemoryClear = () => {
		this.setState({
			...this.state,
			savedNumber: 0,
		});
	};



	handleMemorySubtract = () => {
		let savedNumber = parseFloat(this.state.savedNumber);
		const screenText = parseFloat(this.state.screenText);
		this.setState({
			...this.state,
			savedNumber: savedNumber - screenText,
			isNewNumber: true,
			isFloating: false,
		});
	};

	
	render() {
		return (
			<div style={{
				position: 'absolute', left: '50%', top: '50%',
				transform: 'translate(-50%, -50%)'
			}}>


				<Screen text={this.state.screenText} />
				<Keypad
					onPressDigit={this.handlePressDigit}
					onPressOperator={this.handlePressOperator}
					onPressAC={this.handlePressAC}
					onPressDot={this.handlePressDot}
					onPressNegator={this.handlePressNegator}
					onPressResult={this.handlePressResult}
					onMemorySave={this.handleMemorySave}
					onMemoryPlus={this.handleMemoryPlus}
					onMemorySubtract={this.handleMemorySubtract}
					onMemoryRecall={this.handleMemoryRecall}
					onMemoryClear={this.handleMemoryClear}
				/>
			</div>
		);
	}
}

export default App;
