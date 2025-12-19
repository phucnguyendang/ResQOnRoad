# Frontend (React + Vite + Tailwind CSS v4)

Phần frontend chạy độc lập với backend và chỉ kết nối qua API.

## 1) Yêu cầu môi trường

### Bắt buộc

- Node.js: khuyến nghị dùng bản LTS (Node 20+). Node 18+ thường vẫn chạy được, nhưng nên ưu tiên LTS.
- npm: đi kèm theo Node.js.

### Kiểm tra nhanh

Mở PowerShell tại thư mục `frontend/` và chạy:

```bash
node -v
npm -v
```

## 2) Các thư viện đang dùng

Các thư viện chính hiện đang có trong `package.json`:

- `react`, `react-dom`: React 19
- `vite`, `@vitejs/plugin-react`: dev server + build tool
- `tailwindcss` (v4), `@tailwindcss/postcss`, `postcss`, `autoprefixer`: Tailwind CSS v4 qua PostCSS
- `lucide-react`: icon
- `@testing-library/*`: các thư viện hỗ trợ test UI (hiện dự án chưa cấu hình lại test runner sau khi chuyển sang Vite)

## 3) Cài đặt dependencies

Tại thư mục `frontend/`:

```bash
npm install
```

Nếu bạn vừa kéo code mới hoặc gặp lỗi lạ, có thể “reset” cài đặt:

```bash
rm -r node_modules
rm package-lock.json
npm install
```

(Trên Windows nếu `rm` không có, dùng PowerShell: `Remove-Item -Recurse -Force node_modules` và `Remove-Item package-lock.json`)

## 4) Chạy ở chế độ development

Tại thư mục `frontend/`:

```bash
npm run dev
```

Mặc định dev server chạy tại:

- http://localhost:3000

Lưu ý: cổng 3000 được cấu hình trong `vite.config.js`. Nếu 3000 đang bị chiếm:

- Cách 1: chạy port khác

```bash
npm run dev -- --port 3001
```

- Cách 2: đổi `server.port` trong `vite.config.js`.

## 5) Build production

```bash
npm run build
```

Output nằm trong thư mục `frontend/dist/`.

## 6) Chạy thử bản build (preview)

```bash
npm run preview
```

## 7) Tailwind CSS v4 đang được cấu hình ở đâu?

- `src/index.css` đang dùng:
	- `@import "tailwindcss";`
- `postcss.config.js` đang dùng:
	- `@tailwindcss/postcss` + `autoprefixer`

## 8) Ghi chú về cấu hình API

Hiện thư mục `src/service/` đang trống và chưa thấy file `.env` trong frontend.
Nếu sau này bạn muốn cấu hình base URL cho backend bằng biến môi trường, Vite yêu cầu prefix `VITE_`.
Ví dụ tạo file `.env.local`:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

Và trong code đọc bằng `import.meta.env.VITE_API_BASE_URL`.

## 9) Troubleshooting

### Lỗi Tailwind/PostCSS kiểu “trying to use tailwindcss directly as a PostCSS plugin”

Nếu bạn thấy lỗi này thì gần như chắc chắn bạn đang chạy bằng toolchain không tương thích (ví dụ CRA). Dự án này đã chuyển sang Vite để dùng Tailwind v4.
Hãy đảm bảo bạn chạy `npm run dev` / `npm run build` (Vite), không phải `npm start`.

### Mở trang nhưng không thấy style Tailwind

Kiểm tra:

- `src/index.css` có `@import "tailwindcss";`
- `src/main.jsx` có import `./index.css`
