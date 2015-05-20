$ ->
  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "tweet"
        $("#tweets tbody").append("<tr><td>" + message.uid + "</td><td>" + message.msg + "</td></tr>")
      when "stats"
        $("#stats tbody").html('');
        $("#stats tbody").append("<tr><td>" + wordcount.word + "</td><td>" + wordcount.count + "</td></tr>") for wordcount in message.stats
      else
        console.log(message)

  $("#filterform").submit (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({filter: $("#filter").val()}))
    $("#stats tbody").html('');
    $("#tweets tbody").html('');
    $("#filter").val("")
