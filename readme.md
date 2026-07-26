# SLY Clothing - Nền tảng thương mại điện tử tích hợp AI

SLY Clothing là hệ thống thương mại điện tử cho thương hiệu thời trang SLY. Repo này gồm storefront server-rendered cho
khách hàng, admin dashboard cho nhân viên, cùng AI chatbot và live support qua WebSocket.

## Thành phần chính

- Storefront (Spring Boot + Thymeleaf) cho khách hàng: duyệt sản phẩm, giỏ hàng, đặt hàng, hồ sơ người dùng.
- Admin dashboard (React + Vite) cho nhân viên: quản lý sản phẩm, đơn hàng, khách hàng, thống kê.
- Dịch vụ nền: MySQL, Redis, Azure Blob Storage, SePay, Twilio SMS, SMTP mail, Gemini AI.

## Tính năng

### Storefront (khách hàng)

- Duyệt sản phẩm, lọc theo danh mục, khoảng giá, từ khóa và sắp xếp.
- Giỏ hàng theo session và quy trình đặt hàng.
- Theo dõi đơn hàng với các trạng thái: UNPAID, PAID, PACKAGING, SHIPPING, COMPLETED, CANCELLED, REFUNDED.
- Hồ sơ người dùng, sổ địa chỉ, đổi mật khẩu, xác thực số điện thoại và email.
- Widget chat cho phép chuyển đổi giữa AI và nhân viên hỗ trợ.

### Admin dashboard

- CRUD danh mục và sản phẩm (nhiều ảnh, nhiều size, soft delete/restore).
- Quản lý đơn hàng và cập nhật trạng thái.
- Quản lý khách hàng và nhân viên (role ADMIN/EMPLOYEE/CUSTOMER, vô hiệu hóa tài khoản).
- Thống kê đơn hàng và doanh thu theo ngày/tháng.
- Upload ảnh qua Azure Blob Storage bằng SAS URL.
- Helpdesk chat: hàng chờ phiên hỗ trợ, phân công nhân viên, xem lịch sử chat.

### AI & realtime

- AI chatbot dùng LangChain4j + Gemini (OpenAI-compatible endpoint) với tool tra cứu sản phẩm, danh mục và trạng thái
  đơn.
- WebSocket/STOMP tại `/ws`, subscribe/publish qua `/topic/chat/{sessionId}` và `/topic/support/requests`.
- Lưu lịch sử chat trong bảng `chat_messages`.

### Hạ tầng & vận hành

- Docker Compose cho MySQL, Redis, Azurite, server và admin.
- Terraform triển khai Azure (MySQL, Redis, Storage, Web Apps).
- GitHub Actions build/push Docker image và apply Terraform.

## Sơ đồ C4 - Level 1 - System Context Diagram

[![](https://img.plantuml.biz/plantuml/svg/RPFDRjD058NtaV8Ebwo216BJ9KADtAggA61m-EagcZeNUs0yOuslG_mCR6314ebI8GGI2rP8iLYvuZty9iorqoHskvghw3jdxfpZx6SaBEM96WvkIHsgV8vmtzkxv-tDFAD9I8tsmN0m790aXH2Sd8Ad3CLIHn2GiGY-DMym9BWBGP4H9i0wN18SI15PaR3OHviPFOvopEWEZ8xu7BdXOLoTXp4igWjdfuGStTwmOPwHIT0wobkFEugu-gAZ7HgJL9a2qH67wv7fnt7rLSD2LZyrd5Nd-ffEp1Ef7JZXanLUN9VV8QpBNs8B3mVjqyOPlxYT7SjXj1cqFx7-9Pvbah2zZPpPPmba1QTrSifUBJexND8umrGKx8E-A9hBw_AJ8oaMEWOopgAdY18X5GV89mIFfZu4Q1SoR5vxK9VlESsw_0QHUu44PAxdGU-aaiRXqsQ05sZbwwAlNxgOc_r_X-wUeB-7CP72YUHLcX4cnowAV2vDtsXot2Ru3DLL_zqKNxbF9gjN5nhIk5xzI1XdUFi1TFdB3_Ng3stBONPRVy_RfG2g-WkN7__jji-QTVLTmTEysOGbD-mqUtxLMLTrr8Jjge-DUTklZ-_gu9iIknLTupP5jU2JgN-ptKuN3PXQawIKCRoFUkx-s_y1)](https://editor.plantuml.com/uml/RPFDRjD058NtaV8Ebwo216BJ9KADtAggA61m-EagcZeNUs0yOuslG_mCR6314ebI8GGI2rP8iLYvuZty9iorqoHskvghw3jdxfpZx6SaBEM96WvkIHsgV8vmtzkxv-tDFAD9I8tsmN0m790aXH2Sd8Ad3CLIHn2GiGY-DMym9BWBGP4H9i0wN18SI15PaR3OHviPFOvopEWEZ8xu7BdXOLoTXp4igWjdfuGStTwmOPwHIT0wobkFEugu-gAZ7HgJL9a2qH67wv7fnt7rLSD2LZyrd5Nd-ffEp1Ef7JZXanLUN9VV8QpBNs8B3mVjqyOPlxYT7SjXj1cqFx7-9Pvbah2zZPpPPmba1QTrSifUBJexND8umrGKx8E-A9hBw_AJ8oaMEWOopgAdY18X5GV89mIFfZu4Q1SoR5vxK9VlESsw_0QHUu44PAxdGU-aaiRXqsQ05sZbwwAlNxgOc_r_X-wUeB-7CP72YUHLcX4cnowAV2vDtsXot2Ru3DLL_zqKNxbF9gjN5nhIk5xzI1XdUFi1TFdB3_Ng3stBONPRVy_RfG2g-WkN7__jji-QTVLTmTEysOGbD-mqUtxLMLTrr8Jjge-DUTklZ-_gu9iIknLTupP5jU2JgN-ptKuN3PXQawIKCRoFUkx-s_y1)

## Sơ đồ C4 - Level 2 - Container Diagram

[![](https://img.plantuml.biz/plantuml/svg/VLLDRzD04Br7odyOScgbebvwHAY9KvNIbgQneULKRUnjlDJUjUnrMuCuSq18SE1CIuKG20ISa6fnIjN_uN_2xFeZcxIGowxMxytCldcpMKqaYMKQ1ktM7SRT8FKetBDMbgsL0qjmIHYdyVrsgzsIJ0OKxAsdO0L2-eoFmPOYfZ28nJFgIhWB3G7wZ8nZ4YhUWCQ9u0lZb2Po2JhhQktWPjEVdBi-74ykC590M6UnmRff8aL8OqHPrTPW-9CpFZRGD8m2aL6AYBLggz2F_Cb73iTiyfN3Q7BER_285pAEmAvQ5SFoY_mpk4N-dKp1xPQT9PA61psHSe_4sKAYRbqU8aa9KkfG7dKMuKMx1VXhr5WueQDkbMoFZg19EAG4TIFS0oUBgEt6BDBIh3CYm6D5_ee3Sa5NgegP3PlG-5YBPEkDOjfHh1hJ4qB22PC-E7uMqe2Im_cuHuQOs8Gp6AxP3dG76r15qtUVJKYUfp7j1MA4iNP6kkbA33BMWdVLLr2VjI3LwTRrpnHaNEG_CDStxaDnUS7z6vVfZyhesvczkuKqlKAVI38YYH6-Fe534OC6wSvTdqDIv1_0Kta2TSFqdoc6r6E9LXzNi8Zh6_6t2SV2FT0Vv-gtQP8mmM6v8c62buP1raubjYGYcUx8W6IwiY9_Zm9Bd-3LfK0VAfh168U41QetQWLxsnde1Z1N5zKlyjTgI8hy4r25c6FB4nOmWK17R-09ZTbXDiyynLPZ2KN-oyKCzdm5Gem3scMg2Bs5Bff0fXuJyu6w6wKjXZGmJDtuSBt8tx36I8hym746Kqhr1ZJuVHfYsz0F4Fd5vPVGP2Zyz1reA5TlYylVihJj4hW-aJK9sx9drypfcs3CtZvEDWIJFt3rxhf-3_uJeteVZ02xgIvMXRYjx8PTykkQpQan8fZJqDZFxczY55E3cIYrTHlJbXv6rgtmUfYiuUF-p6ZWsSD-pvedDLv4eLZbDsNcxPlbDCOhiJk8cBhmbdfCasb658imaeafbnuTqqm8m_VI5-9ePe1gkGSxjgFugvHx-0Vs5m00)](https://editor.plantuml.com/uml/VLLDRzD04Br7odyOScgbebvwHAY9KvNIbgQneULKRUnjlDJUjUnrMuCuSq18SE1CIuKG20ISa6fnIjN_uN_2xFeZcxIGowxMxytCldcpMKqaYMKQ1ktM7SRT8FKetBDMbgsL0qjmIHYdyVrsgzsIJ0OKxAsdO0L2-eoFmPOYfZ28nJFgIhWB3G7wZ8nZ4YhUWCQ9u0lZb2Po2JhhQktWPjEVdBi-74ykC590M6UnmRff8aL8OqHPrTPW-9CpFZRGD8m2aL6AYBLggz2F_Cb73iTiyfN3Q7BER_285pAEmAvQ5SFoY_mpk4N-dKp1xPQT9PA61psHSe_4sKAYRbqU8aa9KkfG7dKMuKMx1VXhr5WueQDkbMoFZg19EAG4TIFS0oUBgEt6BDBIh3CYm6D5_ee3Sa5NgegP3PlG-5YBPEkDOjfHh1hJ4qB22PC-E7uMqe2Im_cuHuQOs8Gp6AxP3dG76r15qtUVJKYUfp7j1MA4iNP6kkbA33BMWdVLLr2VjI3LwTRrpnHaNEG_CDStxaDnUS7z6vVfZyhesvczkuKqlKAVI38YYH6-Fe534OC6wSvTdqDIv1_0Kta2TSFqdoc6r6E9LXzNi8Zh6_6t2SV2FT0Vv-gtQP8mmM6v8c62buP1raubjYGYcUx8W6IwiY9_Zm9Bd-3LfK0VAfh168U41QetQWLxsnde1Z1N5zKlyjTgI8hy4r25c6FB4nOmWK17R-09ZTbXDiyynLPZ2KN-oyKCzdm5Gem3scMg2Bs5Bff0fXuJyu6w6wKjXZGmJDtuSBt8tx36I8hym746Kqhr1ZJuVHfYsz0F4Fd5vPVGP2Zyz1reA5TlYylVihJj4hW-aJK9sx9drypfcs3CtZvEDWIJFt3rxhf-3_uJeteVZ02xgIvMXRYjx8PTykkQpQan8fZJqDZFxczY55E3cIYrTHlJbXv6rgtmUfYiuUF-p6ZWsSD-pvedDLv4eLZbDsNcxPlbDCOhiJk8cBhmbdfCasb658imaeafbnuTqqm8m_VI5-9ePe1gkGSxjgFugvHx-0Vs5m00)

## Sơ đồ triển khai - Deployment Diagram

[![](https://img.plantuml.biz/plantuml/svg/fLRBRkCs5Dq7o3-mQL4uG1pPYPqN9kd8zjZ9ZECuVamJO021fXYPZKHgI2gECvXr5qMN_O1PT3aekgsNyoN-avAK9SkFT0hCnfR8Syyzzq5UlHGISXa5FgYMBfcB_NmDXpuR1vZApOtl24L-v6BmebgogYLdldUskP7TXayHnvQjVwii21WzMzszX1HwM1l7m0uMBE88DpYBGh76G365aPpHsc7OnVo18DpsuJhwPz1ha3KeuG0AB69iybfd_7BSlMv-tQA3NIAg40tNiNSbuohE63bx-HgiuhF179e5ixix5OGAZY8SIc5v2eaTGYNc5CTLa4Jw67IRjw3gCpaar0EQ2uCsPxzW944UpEi6QWHw70RQiANgdKkeTi3MYnyMsNSGyna__RwqTrIkbyxUqOlPnfO2zmNc0a3gWWfd8_sojJsZT1cwntmO3JGd2RoJePIXEB4iJ14VXnAxUJcWXLrFzPuaQ1Unm2A1YbtyzFvKxPpOBVUuWJg1L-Zo6tYBQ5-MM_M6yxPyrs_jry-FXbUln-AfztZze_WW4FJnQM5th_nHIQmPt-0y6cYH5veLSEmH8Vau5IXm2CVV9kxwtBxl1xBQ755wE3gyTUlIzYwARdFmgdfJTczBjS1U4TV5RJZMkghJoH-gJ788wH18zkKJJRLv0IJ-ihPOm2v1ZERrZp2ozWvAUrQxrR3AnJtBOyppiQEj7SFX-CnZZaOxnVBnVkbWDwHUAgXe13Kq47GlUsqmEovQNMqw-GqDmSDqyYV06f6AaoFY4_QDPNNiRlsuStl-qBzvKocCIsDslD-wlceMxAD-fLbwkkwjbhLd78Etc9ExyRB6no-VaChbTF8tKh5qvwaqAR79ijxHQBH6f3NWgflLwR52pjm8IPq_OK7Y21cvX5aFHMk41_dZ0pUqJ6xJBSSS5AVuw2Y0em4BkJuic5mRDS2-KATJ6or7OLzeuPiRQ-x6dBao7Euf4xCDadrW09ggoo8vn518O0i2jxR1XyqD0DPSdJaOXew8tvrGBMZIC4mQ06YC9gpiWLoJqEXnEo5ReSjf6iorT_DWGeeFe6ex2GcDry9XH3qP5OwAbj0tKpwsE_6Xn4BgiDUHwrn2Do1QuCzu06pz_BzTAEi5_eywjpCdopT-BXYBzxv3LTEa6Kumu8vnO53lQAxkuqSoqBUj2M1RUpPxu6Yti9IcPvou-bOq4jr1QhugSJvYSbm_EaW_f-hYMUe20pFLWgfi2BjEHUd8obXFfEvuTG2KGI8cpnCcOm2CnQgisP3AYVZVWGYnYCfKrsmVsF6wQSobfSKiPIPVIZfGQpeazJSlSXpbmf9UYS_k3a1G7GI1_LIoKTt1VcR0PHkgnwCnGDD_Ffl3SzxhjRNb8ZxjxlLW0tUQsJvDBHhJoUy4sEsB5OFqp2n5s-xthCQhNe875fXbjFOcKPPrz9_6gyuolLVjz6l0dKx-0ZwPJdwDjD7hMgMQU4bCdkcMk37IFid8MxrNiXy2OUJx8AxMQXBxgTTa_Aa4VYRnz8dlTpfK-JV7Nu-hcS-COJgQOiihXT6LY-T6HkLizcShfU3gVXaoThymaMSgkw1zrUqb3fjpfVFf4dEeiH6eJxGugfUOkkgh-by0)](https://editor.plantuml.com/uml/fLRBRkCs5Dq7o3-mQL4uG1pPYPqN9kd8zjZ9ZECuVamJO021fXYPZKHgI2gECvXr5qMN_O1PT3aekgsNyoN-avAK9SkFT0hCnfR8Syyzzq5UlHGISXa5FgYMBfcB_NmDXpuR1vZApOtl24L-v6BmebgogYLdldUskP7TXayHnvQjVwii21WzMzszX1HwM1l7m0uMBE88DpYBGh76G365aPpHsc7OnVo18DpsuJhwPz1ha3KeuG0AB69iybfd_7BSlMv-tQA3NIAg40tNiNSbuohE63bx-HgiuhF179e5ixix5OGAZY8SIc5v2eaTGYNc5CTLa4Jw67IRjw3gCpaar0EQ2uCsPxzW944UpEi6QWHw70RQiANgdKkeTi3MYnyMsNSGyna__RwqTrIkbyxUqOlPnfO2zmNc0a3gWWfd8_sojJsZT1cwntmO3JGd2RoJePIXEB4iJ14VXnAxUJcWXLrFzPuaQ1Unm2A1YbtyzFvKxPpOBVUuWJg1L-Zo6tYBQ5-MM_M6yxPyrs_jry-FXbUln-AfztZze_WW4FJnQM5th_nHIQmPt-0y6cYH5veLSEmH8Vau5IXm2CVV9kxwtBxl1xBQ755wE3gyTUlIzYwARdFmgdfJTczBjS1U4TV5RJZMkghJoH-gJ788wH18zkKJJRLv0IJ-ihPOm2v1ZERrZp2ozWvAUrQxrR3AnJtBOyppiQEj7SFX-CnZZaOxnVBnVkbWDwHUAgXe13Kq47GlUsqmEovQNMqw-GqDmSDqyYV06f6AaoFY4_QDPNNiRlsuStl-qBzvKocCIsDslD-wlceMxAD-fLbwkkwjbhLd78Etc9ExyRB6no-VaChbTF8tKh5qvwaqAR79ijxHQBH6f3NWgflLwR52pjm8IPq_OK7Y21cvX5aFHMk41_dZ0pUqJ6xJBSSS5AVuw2Y0em4BkJuic5mRDS2-KATJ6or7OLzeuPiRQ-x6dBao7Euf4xCDadrW09ggoo8vn518O0i2jxR1XyqD0DPSdJaOXew8tvrGBMZIC4mQ06YC9gpiWLoJqEXnEo5ReSjf6iorT_DWGeeFe6ex2GcDry9XH3qP5OwAbj0tKpwsE_6Xn4BgiDUHwrn2Do1QuCzu06pz_BzTAEi5_eywjpCdopT-BXYBzxv3LTEa6Kumu8vnO53lQAxkuqSoqBUj2M1RUpPxu6Yti9IcPvou-bOq4jr1QhugSJvYSbm_EaW_f-hYMUe20pFLWgfi2BjEHUd8obXFfEvuTG2KGI8cpnCcOm2CnQgisP3AYVZVWGYnYCfKrsmVsF6wQSobfSKiPIPVIZfGQpeazJSlSXpbmf9UYS_k3a1G7GI1_LIoKTt1VcR0PHkgnwCnGDD_Ffl3SzxhjRNb8ZxjxlLW0tUQsJvDBHhJoUy4sEsB5OFqp2n5s-xthCQhNe875fXbjFOcKPPrz9_6gyuolLVjz6l0dKx-0ZwPJdwDjD7hMgMQU4bCdkcMk37IFid8MxrNiXy2OUJx8AxMQXBxgTTa_Aa4VYRnz8dlTpfK-JV7Nu-hcS-COJgQOiihXT6LY-T6HkLizcShfU3gVXaoThymaMSgkw1zrUqb3fjpfVFf4dEeiH6eJxGugfUOkkgh-by0)

## Sơ đồ lớp - Class Diagram

[![](https://img.plantuml.biz/plantuml/svg/hLTRJzim57wlrFzWySI6Xk0LJMXP6rYrjaIKJaBYnKqEhKLYPxOZr4txxxDZ9t4Sqp547r3EyRbVFfilGc8kgo8VZyQZ7Sa0GL7AFIgWs00Nuv5a9P8CRPYKh40PuP1AmkXu99u8BJ771KfpB4Ga9IURIi8iPNH5VW4wqoRhCpJ1d5K2SlHxF4BefAMFFsZw4oBP1LglPrD3II9pk40hPP1k3KlPb41bdvaJ-kHnCCr4YakO5NZhcIWPbnmJ6JXAEM09MQJipwWg29M6NvNPWF-doqd2bl5zbvEXmpbHNBo7qnkU0MyyraJO7TD74vPvFbNDkH9JVnsngNBdnI1sf2oLLk18Cedp2_ILRAUGaWBdDZyc_VmmJI7FiPwMMy22qVVAVmeIaxnN1SCwK8iB8sT7WREiIgMUqdvCFolbZiYzTgswsScJ58OvYseJg8QA5MXL67y9tXTgRXSWTooBDVlpPwIFWTU5GPUNJHwhE8dk_LHNjX5Hbd5GpDzEIhD_35YvOzJlIPsRoyc8K5JgRTOpvbcVWqq0FUOdfm5UeBOJLa8oopRAnnywCxrghIIMbJXSflKoYKpE9yZzcdoFhcVBQrlBRxCaQQd9pIAPntUnbRsDhzRBQKDDekKadiyrsPO-CVFHkBJaQnSx0v5oKkgu_OtWe4UQLpM42gzTuI4ZT2LP-kHDf6fRoYeDY5SvmzAnC6rFV4l_it0cZnnUNCIaMo6lJ9hrsbA5atiJCbZlDRfx4TMyS4YFP73J19p_wMwoEuwfm7N_6YyEwuNxh1COGZA7H-2We79uj5M1FEFzSdXrX6v3N8JRxdWtS3vdASxrvnqfu63I9cLzNJJ0yw-61Z072eNCdfGo-ynyYDa2pPhhwRh-raXWpZQ4ovqNlkFn1t3oI08ZyzypJmJUvCC32sGwj3wq4P6u0MXesp2M0wPU1Ni8VkAomaFplb2jufp28wvoFI1DV6_OjHgh-mrw6QeNqJmsNrUn-NOjtAee3kjFrgkxcqLyQwXOWVZDVMmjHjF5R6atits9sTzHQXW4n0CzEptzUAJQXOxihT_oB3fe2mtWjnQix0Czx-bgRFFrDU_r5ceiKYRENHCrynKsMFVKQQDmdZyw4_D0x7v61nbt7xMHSqzde75k1LdWNgz32Ltu1-mu0DH6xM1cGBFfsQKgWVk0whmFtbM7hOZwOU44tpuqTCrgW7kn0lrA7v1kv9pL3aWxKHd0gwFoCU-G6bUBqoerMpGUVL7gwl-ulm00)](https://editor.plantuml.com/uml/hLTRJzim57wlrFzWySI6Xk0LJMXP6rYrjaIKJaBYnKqEhKLYPxOZr4txxxDZ9t4Sqp547r3EyRbVFfilGc8kgo8VZyQZ7Sa0GL7AFIgWs00Nuv5a9P8CRPYKh40PuP1AmkXu99u8BJ771KfpB4Ga9IURIi8iPNH5VW4wqoRhCpJ1d5K2SlHxF4BefAMFFsZw4oBP1LglPrD3II9pk40hPP1k3KlPb41bdvaJ-kHnCCr4YakO5NZhcIWPbnmJ6JXAEM09MQJipwWg29M6NvNPWF-doqd2bl5zbvEXmpbHNBo7qnkU0MyyraJO7TD74vPvFbNDkH9JVnsngNBdnI1sf2oLLk18Cedp2_ILRAUGaWBdDZyc_VmmJI7FiPwMMy22qVVAVmeIaxnN1SCwK8iB8sT7WREiIgMUqdvCFolbZiYzTgswsScJ58OvYseJg8QA5MXL67y9tXTgRXSWTooBDVlpPwIFWTU5GPUNJHwhE8dk_LHNjX5Hbd5GpDzEIhD_35YvOzJlIPsRoyc8K5JgRTOpvbcVWqq0FUOdfm5UeBOJLa8oopRAnnywCxrghIIMbJXSflKoYKpE9yZzcdoFhcVBQrlBRxCaQQd9pIAPntUnbRsDhzRBQKDDekKadiyrsPO-CVFHkBJaQnSx0v5oKkgu_OtWe4UQLpM42gzTuI4ZT2LP-kHDf6fRoYeDY5SvmzAnC6rFV4l_it0cZnnUNCIaMo6lJ9hrsbA5atiJCbZlDRfx4TMyS4YFP73J19p_wMwoEuwfm7N_6YyEwuNxh1COGZA7H-2We79uj5M1FEFzSdXrX6v3N8JRxdWtS3vdASxrvnqfu63I9cLzNJJ0yw-61Z072eNCdfGo-ynyYDa2pPhhwRh-raXWpZQ4ovqNlkFn1t3oI08ZyzypJmJUvCC32sGwj3wq4P6u0MXesp2M0wPU1Ni8VkAomaFplb2jufp28wvoFI1DV6_OjHgh-mrw6QeNqJmsNrUn-NOjtAee3kjFrgkxcqLyQwXOWVZDVMmjHjF5R6atits9sTzHQXW4n0CzEptzUAJQXOxihT_oB3fe2mtWjnQix0Czx-bgRFFrDU_r5ceiKYRENHCrynKsMFVKQQDmdZyw4_D0x7v61nbt7xMHSqzde75k1LdWNgz32Ltu1-mu0DH6xM1cGBFfsQKgWVk0whmFtbM7hOZwOU44tpuqTCrgW7kn0lrA7v1kv9pL3aWxKHd0gwFoCU-G6bUBqoerMpGUVL7gwl-ulm00)

## Sơ đồ cơ sở dữ liệu - Database Diagram

[![](https://img.plantuml.biz/plantuml/svg/lLVDRk8m4BuZyGvMBr2fI0kxhIfKePxsqik-GMJi0QmwTkgVTjcsxxxZn26_b9Q5pO4adiduc_7CDyEzTTGudyhXO3YGi7B4QMB4UkC85mQO4reD1nl1WK2QkIr98Ls2iK4Tb1EumgZHte8ahyC18KJmELaAHSrsFBsv97TtlnuM2xojGO6X3lXYaUjD9laDJKYOasTgs8QQyVVPPRxCj79elnBS_2W4KgZ7QdKwkutBL77BQ0Q9IEaQUZGoRPoXmkrLO0O2l8JYhawaW959inxWFkE7zTvhuN6mraQ0FIv0YgRG20EvyqeyULZy1zJR93EQU-OYzjtoFYT-FiJzudS75AiDjU5W4WwE2dbaf0eJdOyEG-feMF6d5lEOUK-Uvb3dH2WNrGL3FGuCSqgEfxTNCNkjNphjgEp8slxQZSWoeTO9vTo0jRiasUDBEygzELAb_4Qh-lgtcEYv4-sAuy8wzCTr9I_Ky1xh4N9RqdBnoBD4GldeA8lmCCrhxisYUp6Jq2Q67tz3jXUdKWQxHvRHROgmanJSHkEse7mw7bd0zL4yKqUTjwN4gumAFheYe-hE7kaQpOUNSglmp7IQIK0FynUg64XPl1XOUSNnkTYXFsN2yUg0rmcrTV3RnKBCxT30pmbIWDMghT573Fkfe7vmkq89J7da0HukjfMGHzDm5siesRD1RTPNeE2IWmkvK8brcZrMr70cMevGJz9APjVN7KSuM6P45h8AyLJ9Ph2lAkG7dmy4jYRRou7Tc233CEr3Krz9JMkV9vMaQ-UaaSnTp8jnV_RZbA0iBHmzJoEgxT3Yi6diDSWCO01Bl2KlANcDcxxGRT8yqSxmj3ibckuBRnrB3oz-iHfpscxtijDI_xzrhtArqxpA-L9O749Mr4jNy2acxtXwrW4n-7kIWlwWxxVh758SirfJPLCZezQ-w7he0byKoQa3SHTza7aiyXHCQ6Vu1q7gBK1ufZmLAlOr18_rK7w1wPThh21_AbIMmhXDBVygDv9dC68bUXlPoQb1MBgKlTmm77GcujtrhYUJjpTId_IBQqukwWEpu1UVCRFZU5APgRMet4fpMgvVxs-5bJXyaRgL7IyTil5b93rpB7b39FhrqqYQ45f8YXffsYXv8ZVH6bbw0l8PPvh3nB4XQJIGNYk7Gz8YqcD2SezbXt_i_m80)](https://editor.plantuml.com/uml/lLVDRk8m4BuZyGvMBr2fI0kxhIfKePxsqik-GMJi0QmwTkgVTjcsxxxZn26_b9Q5pO4adiduc_7CDyEzTTGudyhXO3YGi7B4QMB4UkC85mQO4reD1nl1WK2QkIr98Ls2iK4Tb1EumgZHte8ahyC18KJmELaAHSrsFBsv97TtlnuM2xojGO6X3lXYaUjD9laDJKYOasTgs8QQyVVPPRxCj79elnBS_2W4KgZ7QdKwkutBL77BQ0Q9IEaQUZGoRPoXmkrLO0O2l8JYhawaW959inxWFkE7zTvhuN6mraQ0FIv0YgRG20EvyqeyULZy1zJR93EQU-OYzjtoFYT-FiJzudS75AiDjU5W4WwE2dbaf0eJdOyEG-feMF6d5lEOUK-Uvb3dH2WNrGL3FGuCSqgEfxTNCNkjNphjgEp8slxQZSWoeTO9vTo0jRiasUDBEygzELAb_4Qh-lgtcEYv4-sAuy8wzCTr9I_Ky1xh4N9RqdBnoBD4GldeA8lmCCrhxisYUp6Jq2Q67tz3jXUdKWQxHvRHROgmanJSHkEse7mw7bd0zL4yKqUTjwN4gumAFheYe-hE7kaQpOUNSglmp7IQIK0FynUg64XPl1XOUSNnkTYXFsN2yUg0rmcrTV3RnKBCxT30pmbIWDMghT573Fkfe7vmkq89J7da0HukjfMGHzDm5siesRD1RTPNeE2IWmkvK8brcZrMr70cMevGJz9APjVN7KSuM6P45h8AyLJ9Ph2lAkG7dmy4jYRRou7Tc233CEr3Krz9JMkV9vMaQ-UaaSnTp8jnV_RZbA0iBHmzJoEgxT3Yi6diDSWCO01Bl2KlANcDcxxGRT8yqSxmj3ibckuBRnrB3oz-iHfpscxtijDI_xzrhtArqxpA-L9O749Mr4jNy2acxtXwrW4n-7kIWlwWxxVh758SirfJPLCZezQ-w7he0byKoQa3SHTza7aiyXHCQ6Vu1q7gBK1ufZmLAlOr18_rK7w1wPThh21_AbIMmhXDBVygDv9dC68bUXlPoQb1MBgKlTmm77GcujtrhYUJjpTId_IBQqukwWEpu1UVCRFZU5APgRMet4fpMgvVxs-5bJXyaRgL7IyTil5b93rpB7b39FhrqqYQ45f8YXffsYXv8ZVH6bbw0l8PPvh3nB4XQJIGNYk7Gz8YqcD2SezbXt_i_m80)

## Công nghệ

- Backend: Java 21, Spring Boot 4.0.6, Spring MVC, Spring Security (JWT cho `/api/**` + form login cho storefront),
  Spring Data JPA, Flyway.
- Storefront: Thymeleaf + Tailwind (build tại `server/src/main/frontend`).
- Admin dashboard: React 19, Vite, TypeScript, Tailwind, Recharts, STOMP.
- Database: MySQL.
- Cache/Session/Rate limit/Streams: Redis.
- Storage: Azure Blob Storage (SAS).
- Payment: SePay (QR + invoice).
- Messaging: Twilio SMS OTP, SMTP mail.

## API & WebSocket chính

- Auth (admin): `POST /api/auth/login`
- Users: `GET /api/users/profile`, `PUT /api/users/profile`, `POST /api/users/profile/email/verify`,
  `PUT /api/users/change-password`
- Products (admin): `GET|POST /api/products`, `PATCH|DELETE /api/products/{id}`
- Categories (admin): `GET|POST /api/categories`, `PATCH|DELETE /api/categories/{id}`
- Orders (admin): `GET /api/orders`, `PATCH /api/orders/{id}/status`
- Customers (admin): `GET /api/customers`, `PATCH /api/customers/{id}/status`
- Employees (admin): `GET|POST /api/employees`, `PATCH /api/employees/{id}`, `PATCH /api/employees/{id}/status`
- Dashboard (admin): `GET /api/admin/dashboard/stats`, `GET /api/admin/dashboard/order-stats`,
  `GET /api/admin/dashboard/revenue-stats`
- Storage (admin): `GET /api/blobs/{container}/sas`, `DELETE /api/blobs/{container}/{blob}`
- AI chat: `POST /api/chat`
- Support chat: `POST /api/support/request`, `POST /api/support/assign`, `GET /api/support/sessions`
- Chat history: `GET /api/chat/history/{sessionId}`
- Transactions: `POST /api/transactions`
- WebSocket: endpoint `/ws`, app destination `/app/chat.send`, topics `/topic/chat/{sessionId}`,
  `/topic/support/requests`

Lưu ý: `/api/**` yêu cầu JWT; một số endpoint giới hạn quyền ADMIN/EMPLOYEE theo `@PreAuthorize`.

## Route web chính (storefront)

- `GET /` (trang chủ), `GET /products`, `GET /products/{id}`
- `GET /cart`, `POST /cart/items`, `POST /cart/items/{itemId}/update`, `POST /cart/items/{itemId}/remove`
- `GET /orders`, `POST /orders`, `GET /orders/{id}`, `POST /orders/{id}/cancel`
- `GET /auth/login`, `GET|POST /auth/register`, `GET|POST /auth/change-password`, `GET|POST /auth/verify-phone`
- `GET /profile`, `GET /profile/update`, `POST /profile/update`, `POST /profile/email/verify`

## Cấu trúc thư mục

- `server/`: Spring Boot backend + storefront (Thymeleaf, static assets, migrations).
- `web/`: admin dashboard (React + Vite).
- `.docs/`: sơ đồ kiến trúc và tài liệu (PlantUML, HTML).
- `terraform/`: hạ tầng Azure.
- `.github/workflows/`: CI/CD.
- `compose.yaml`: Docker Compose cho local.

## Cấu hình môi trường

Tham khảo `server/.env.example` và `compose.yaml`. Các nhóm biến chính:

- MySQL: `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD`
- DB/App: `DATABASE_CONNECTION_STRING`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- Redis: `REDIS_CONNECTION_STRING`
- JWT: `JWT_SECRET`, `JWT_EXPIRATION_MS`
- CORS: `ALLOWED_ORIGINS`, `ALLOWED_METHODS`, `ALLOWED_HEADERS`, `ALLOW_CREDENTIALS`
- SePay: `SEPAY_*` (bank, account, invoice urls, username/password)
- Twilio: `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_VERIFY_SERVICE_SID`
- Mail: `MAIL_USERNAME`, `MAIL_PASSWORD`
- Azure Storage: `AZURE_STORAGE_CONNECTION_STRING`
- AI: `GEMINI_API_KEY`

Admin dashboard (build) dùng:

- `VITE_API_URL`
- `VITE_IMAGE_CONTAINER_URL`
- `VITE_STORAGE_URL`

## Chạy local

### Option A: Docker Compose toàn bộ

```bash
docker compose up -d
```

### Option B: Chạy dịch vụ nền + chạy ứng dụng thủ công

```bash
docker compose -f server/compose.yaml up -d
```

```bash
cd server
./mvnw spring-boot:run
```

```bash
cd web
npm install
npm run dev
```

Tùy chọn build CSS storefront (Tailwind):

```bash
cd server/src/main/frontend
npm install
npm run watch
```

Mặc định:

- Server: `http://localhost:8080`
- Admin: `http://localhost:5173`
