jvmsniffer
==========

Logs network requests/responses in the JVM to a file.


Usage
-----

Call JvmSniffer.init(String aFile) as early in startup as possible.


How it works
------------

Registers a special URLStreamHandlerFactory, which in turn wraps real URLStreamHandlers with sniffer handlers, which log requests/responses.

As all network traffic goes through these handlers (even images, media, etc.), only requests/responses whose Content-Type header is set to a printable content type are logged. Currently it prints anything with a Content-Type value including the words "text", "json" or "xml".

Normally real URLStreamHandlers are hidden implementations, so jvmsniffer heavily relies on reflection and common class names to get the real handlers. If a handler is not found, it should fall back to default JVM implementation and the network traffic will simply not be logged.

Tested on Android 4.3.
