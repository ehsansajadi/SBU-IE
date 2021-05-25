const Key = ({ onClick, text, wide, blue, red }) => {
	return (
		<button
			onClick={onClick}
			className={[
				"key",
				wide && "wide",
				blue && "blue",
				red && "red",
			].join(" ")}
		>
			{text}
		</button>
	);
};

export default Key;
