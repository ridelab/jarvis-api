(() => {
  const uri = 'ws://localhost:9000/ws'
  const socket = new WebSocket(uri)
  socket.addEventListener('message', event => {
    const message = JSON.parse(event.data)
    console.log(message)
  })
})()
