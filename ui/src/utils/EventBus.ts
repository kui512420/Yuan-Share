import mitt from 'mitt'

type Events = {
  'add-image-to-chat': string;
  [key: string]: any
}

const emitter = mitt<Events>()

export default emitter 