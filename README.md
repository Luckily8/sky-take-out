# 购物交互平台（测试开发能力展示）

## 项目简介
...

## 技术架构
- SSM后端框架
- JWT鉴权
- MySQL+Redis
- Swagger+ApiFox
- Python+pytest自动化测试
- GitHub Actions 持续集成

## 测试开发能力亮点

- 自动化接口测试（pytest+requests）
- Web自动化流程测试（selenium）
- Swagger一键生成接口测试用例
- CI/CD自动触发测试与代码扫描
- Mock服务与测试环境支持

## 快速体验

```bash
# 运行接口测试
cd autotest
pytest --alluredir=./results
allure serve ./results

# 本地部署测试环境
docker-compose up -d
