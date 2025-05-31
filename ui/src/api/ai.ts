import Request from '@/utils/Request'
//添加文章

interface GenerateImageParams {
  prompt: string;
  size?: string;
  style?: string;
}

export function generate(prompt: string, size?: string){
  const params: GenerateImageParams = {
    prompt
  }
  if (size) params.size = size
  return Request({
    url: 'api/ai/generate?prompt='+prompt+"&size="+size,
    method: 'get'
  })
}
