package net.twisterrob.android.log

fun getLogsFor(tag: String, format: String = "tag"): List<String> {
	// -d: dump and stop, don't block
	// -b main: app's logs
	// -v ...: output format
	// -s: silent by default
	// tag:level: only interested in logs for specific tag
	val command = "logcat -d -b main -v ${format} -s ${tag}"
	val process = Runtime.getRuntime().exec(command)
	val exit = process.waitFor()
	val error = process.errorStream
		.bufferedReader()
		.readText()
		.takeIf(String::isNotEmpty)
	if (error != null || exit != 0) {
		error("Error running command: ${command}\nExit: ${exit}\n${error}")
	}
	val output = process.inputStream
		.bufferedReader()
		.lineSequence()
		.toList()
	return output.dropWhile { it == "--------- beginning of main" }
}
