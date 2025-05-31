export function convertDate(dateStr:string) {
  // 创建一个 Date 对象
  const date = new Date(dateStr);
  // 调整时区
  date.setUTCHours(date.getUTCHours());

  // 获取年、月、日、时、分、秒
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  // 拼接成所需格式
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

